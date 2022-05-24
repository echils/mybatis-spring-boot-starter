package com.github.mybatis.statement.loader;

import com.github.mybatis.specification.SpecificationMapper;
import com.github.mybatis.statement.metadata.ColumnMetaData;
import com.github.mybatis.statement.metadata.MappedMetaData;
import com.github.mybatis.statement.metadata.TableMetaData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.*;
import org.apache.ibatis.session.Configuration;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.mybatis.MybatisExpandContext.*;

/**
 * 通过主键批量查询功能加载器
 *
 * @author echils
 */
@Slf4j
public class SelectByPrimaryKeysStatementLoader extends AbstractExpandStatementLoader {

    /**
     * 拓展方法名
     */
    private static final String EXPAND_STATEMENT_METHOD
            = SpecificationMapper.class.getName() + ".selectByPrimaryKeys";

    @Override
    SqlCommandType sqlCommandType() {
        return SqlCommandType.SELECT;
    }

    @Override
    SqlSource sqlSourceBuild(MappedMetaData mappedMetaData) {

        Configuration configuration =
                mappedMetaData.getMapperFactoryBean().getSqlSession().getConfiguration();
        TableMetaData tableMetaData = mappedMetaData.getTableMetaData();
        List<SqlNode> sqlNodes = new LinkedList<>();
        sqlNodes.add(new StaticTextSqlNode("SELECT "));
        sqlNodes.add(new StaticTextSqlNode(mappedMetaData.getBaseColumnList()));
        sqlNodes.add(new StaticTextSqlNode(" FROM "
                + KEYWORDS_ESCAPE_FUNCTION.apply(tableMetaData.getName())));
        sqlNodes.add(new StaticTextSqlNode(" WHERE 1=1"));

        if (StringUtils.isNotBlank(mappedMetaData.getWhereClause())) {
            sqlNodes.add(new StaticTextSqlNode(" AND " + mappedMetaData.getWhereClause()));
        }
        List<ColumnMetaData> columnMetaDataList = tableMetaData.getColumnMetaDataList();
        columnMetaDataList.stream().filter(ColumnMetaData::isLogical).forEach(columnMetaData ->
                sqlNodes.add(new StaticTextSqlNode(" AND " + columnMetaData.getColumnName() + "=" + columnMetaData.getExistValue())));
        List<ColumnMetaData> primaryKeyColumnDataList = columnMetaDataList
                .stream().filter(ColumnMetaData::isPrimaryKey).collect(Collectors.toList());

        //联合主键
        if (primaryKeyColumnDataList.size() > UNIQUE_PRIMARY_KEY_INDEX) {
            Map<String, String> primaryMap = primaryKeyColumnDataList.stream().collect(Collectors.toMap(
                    columnMetaData -> KEYWORDS_ESCAPE_FUNCTION.apply(columnMetaData.getColumnName()),
                    columnMetaData -> String.format(MYBATIS_PARAM_SIMPLE_EXPRESSION, MYBATIS_FOREACH_PARAM + "." + columnMetaData.getFieldName())));
            ArrayList<String> columns = new ArrayList<>();
            ArrayList<String> fields = new ArrayList<>();
            primaryMap.forEach((key, value) -> { columns.add(key); fields.add(value); });
            ArrayList<String> blanks = new ArrayList<>();
            for (int i = 0; i < primaryMap.keySet().size(); i++) { blanks.add("''");}
            sqlNodes.add(new StaticTextSqlNode(" AND "));
            sqlNodes.add(new StaticTextSqlNode("(" + String.join(",", String.join(",", columns) + ") IN ")));
            sqlNodes.add(new ChooseSqlNode(Collections.singletonList(new IfSqlNode(
                    new ForEachSqlNode(configuration, new StaticTextSqlNode("( " + String.join(",", fields) + ") "),
                            "collection", null, MYBATIS_FOREACH_PARAM,
                            "(", ")", ","),
                    "collection != null && collection.size > 0")), new StaticTextSqlNode("((" + String.join(",", blanks) + "))")));
        } else {
            //唯一主键
            ColumnMetaData columnMetaData = primaryKeyColumnDataList.get(0);
            sqlNodes.add(new StaticTextSqlNode(" AND " + KEYWORDS_ESCAPE_FUNCTION
                    .apply(columnMetaData.getColumnName()) + " IN "));
            sqlNodes.add(new ChooseSqlNode(Collections.singletonList(new IfSqlNode(
                    new ForEachSqlNode(configuration, new StaticTextSqlNode("#{item}"),
                            "collection", null, MYBATIS_FOREACH_PARAM,
                            "(", ")", ","),
                    "collection != null && collection.size > 0")), new StaticTextSqlNode("('')")));
        }

        return new DynamicSqlSource(configuration, new MixedSqlNode(sqlNodes));
    }

    @Override
    public boolean match(MappedMetaData mappedMetaData) {
        return mappedMetaData.getMappedMethod().toString().contains(EXPAND_STATEMENT_METHOD);
    }

}
