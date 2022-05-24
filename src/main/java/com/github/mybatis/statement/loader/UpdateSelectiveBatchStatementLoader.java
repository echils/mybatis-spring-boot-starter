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
 * 选择批量更新功能加载器
 *
 * @author echils
 */
@Slf4j
public class UpdateSelectiveBatchStatementLoader extends AbstractExpandStatementLoader {

    /**
     * 拓展方法名
     */
    private static final String EXPAND_STATEMENT_METHOD
            = SpecificationMapper.class.getName() + ".updateSelectiveBatch";

    @Override
    SqlCommandType sqlCommandType() {
        return SqlCommandType.UPDATE;
    }

    @Override
    SqlSource sqlSourceBuild(MappedMetaData mappedMetaData) {

        Configuration configuration =
                mappedMetaData.getMapperFactoryBean().getSqlSession().getConfiguration();
        TableMetaData tableMetaData = mappedMetaData.getTableMetaData();
        List<SqlNode> sqlNodeList = new LinkedList<>();
        sqlNodeList.add(new StaticTextSqlNode("UPDATE "));
        sqlNodeList.add(new StaticTextSqlNode(KEYWORDS_ESCAPE_FUNCTION.apply(tableMetaData.getName())));

        List<ColumnMetaData> columnMetaDataList = tableMetaData.getColumnMetaDataList();
        List<ColumnMetaData> primaryKeyColumnList
                = columnMetaDataList.stream().filter(ColumnMetaData::isPrimaryKey)
                .collect(Collectors.toList());

        columnMetaDataList.removeAll(primaryKeyColumnList);
        List<SqlNode> setSqlNodeList = columnMetaDataList.stream().filter(columnMetaData
                -> columnMetaData.isUpdatable() && !columnMetaData.isLogical()).map(columnMetaData ->
                StringUtils.isNotBlank(columnMetaData.getDefaultUpdateValue()) ?
                        new StaticTextSqlNode(KEYWORDS_ESCAPE_FUNCTION.apply(columnMetaData.getColumnName())
                                + " = " + columnMetaData.getDefaultUpdateValue() + ", ") :
                        new IfSqlNode(new StaticTextSqlNode(KEYWORDS_ESCAPE_FUNCTION.apply(columnMetaData.getColumnName())
                                + " = " + String.format(MYBATIS_PARAM_EXPRESSION,
                                "param2." + columnMetaData.getFieldName(), columnMetaData.getJdbcType()) + ", "),
                                String.format(MYBATIS_TEST_EXPRESSION, "param2." + columnMetaData.getFieldName())))
                .collect(Collectors.toList());
        sqlNodeList.add(new IfSqlNode(new SetSqlNode(configuration, new TrimSqlNode(configuration, new MixedSqlNode(setSqlNodeList),
                null, null, null, ",")), "param2 != null"));

        List<SqlNode> keySqlNodeList = new LinkedList<>();
        keySqlNodeList.add(new StaticTextSqlNode(" 1 = 1"));
        if (StringUtils.isNotBlank(mappedMetaData.getWhereClause())) {
            keySqlNodeList.add(new StaticTextSqlNode(" AND " + mappedMetaData.getWhereClause()));
        }

        //联合主键
        if (primaryKeyColumnList.size() > UNIQUE_PRIMARY_KEY_INDEX) {
            Map<String, String> primaryMap = primaryKeyColumnList.stream().collect(Collectors.toMap(
                    columnMetaData -> KEYWORDS_ESCAPE_FUNCTION.apply(columnMetaData.getColumnName()),
                    columnMetaData -> String.format(MYBATIS_PARAM_SIMPLE_EXPRESSION, MYBATIS_FOREACH_PARAM + "." + columnMetaData.getFieldName())));
            ArrayList<String> columns = new ArrayList<>();
            ArrayList<String> fields = new ArrayList<>();
            primaryMap.forEach((key, value) -> {
                columns.add(key);
                fields.add(value);
            });
            ArrayList<String> blanks = new ArrayList<>();
            for (int i = 0; i < primaryMap.keySet().size(); i++) {
                blanks.add("''");
            }
            keySqlNodeList.add(new StaticTextSqlNode(" AND "));
            keySqlNodeList.add(new StaticTextSqlNode("(" + String.join(",", String.join(",", columns) + ") IN ")));
            keySqlNodeList.add(new ChooseSqlNode(Collections.singletonList(new IfSqlNode(
                    new ForEachSqlNode(configuration, new StaticTextSqlNode("( " + String.join(",", fields) + ") "),
                            "param1", null, MYBATIS_FOREACH_PARAM,
                            "(", ")", ","),
                    "param1 != null && param1.size > 0")), new StaticTextSqlNode("((" + String.join(",", blanks) + "))")));
            columnMetaDataList.stream().filter(ColumnMetaData::isLogical).forEach(metaData ->
                    keySqlNodeList.add(new StaticTextSqlNode(" AND " + metaData.getColumnName() + "=" + metaData.getExistValue())));
        } else {
            //唯一主键
            ColumnMetaData columnMetaData = primaryKeyColumnList.get(0);
            keySqlNodeList.add(new StaticTextSqlNode(" AND " + KEYWORDS_ESCAPE_FUNCTION
                    .apply(columnMetaData.getColumnName()) + " IN "));
            keySqlNodeList.add(new ChooseSqlNode(Collections.singletonList(new IfSqlNode(
                    new ForEachSqlNode(configuration, new StaticTextSqlNode("#{item}"),
                            "param1", null, MYBATIS_FOREACH_PARAM,
                            "(", ")", ","),
                    "param1 != null && param1.size > 0")), new StaticTextSqlNode("('')")));
            columnMetaDataList.stream().filter(ColumnMetaData::isLogical).forEach(metaData ->
                    keySqlNodeList.add(new StaticTextSqlNode(" AND " + metaData.getColumnName() + "=" + metaData.getExistValue())));
        }

        sqlNodeList.add(new WhereSqlNode(configuration, new MixedSqlNode(keySqlNodeList)));
        return new DynamicSqlSource(configuration, new MixedSqlNode(sqlNodeList));
    }

    @Override
    public boolean match(MappedMetaData mappedMetaData) {
        return mappedMetaData.getMappedMethod().toString().contains(EXPAND_STATEMENT_METHOD);
    }

}
