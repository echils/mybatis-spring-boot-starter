package com.github.mybatis.statement.loader;

import com.github.mybatis.specification.SpecificationMapper;
import com.github.mybatis.statement.metadata.ColumnMetaData;
import com.github.mybatis.statement.metadata.MappedMetaData;
import com.github.mybatis.statement.metadata.TableMetaData;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.*;
import org.apache.ibatis.session.Configuration;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.mybatis.MybatisExpandContext.*;
import static com.github.mybatis.MybatisExpandContext.MYBATIS_PARAM_EXPRESSION;

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
        List<SqlNode> setSqlNodeList = columnMetaDataList.stream().map(columnMetaData ->
                new IfSqlNode(new StaticTextSqlNode(KEYWORDS_ESCAPE_FUNCTION
                        .apply(columnMetaData.getColumnName()) + " = " + String.format(MYBATIS_PARAM_EXPRESSION,
                        "param2." + columnMetaData.getFieldName(), columnMetaData.getJdbcType()) + ", "),
                        String.format(MYBATIS_TEST_EXPRESSION, "param2." + columnMetaData.getFieldName())))
                .collect(Collectors.toList());
        sqlNodeList.add(new SetSqlNode(configuration, new TrimSqlNode(configuration, new MixedSqlNode(setSqlNodeList),
                null, null, null, ",")));

        List<SqlNode> keySqlNodeList = new LinkedList<>();
        //唯一主键
        if (primaryKeyColumnList.size() == UNIQUE_PRIMARY_KEY_INDEX) {
            ColumnMetaData columnMetaData = primaryKeyColumnList.get(0);
            keySqlNodeList.add(new StaticTextSqlNode(" AND " + KEYWORDS_ESCAPE_FUNCTION
                    .apply(columnMetaData.getColumnName()) + " IN "));
            keySqlNodeList.add(new ForEachSqlNode(configuration, new StaticTextSqlNode("#{item}"),
                    "param1", null, MYBATIS_FOREACH_PARAM,
                    "(", ")", ","));
        } else {
        //联合主键
            List<String> columnList = primaryKeyColumnList.stream().map(columnMetaData
                    -> MYBATIS_FOREACH_PARAM + "." + KEYWORDS_ESCAPE_FUNCTION.apply(columnMetaData.getColumnName()))
                    .collect(Collectors.toList());
            String condition = String.join(" AND ", columnList);
            keySqlNodeList.add(new ForEachSqlNode(configuration, new StaticTextSqlNode(" AND " + condition),
                    "param1", "index", MYBATIS_FOREACH_PARAM,
                    "(", ")", " OR "));
        }
        sqlNodeList.add(new WhereSqlNode(configuration, new MixedSqlNode(keySqlNodeList)));
        return new DynamicSqlSource(configuration, new MixedSqlNode(sqlNodeList));
    }

    @Override
    public boolean match(MappedMetaData mappedMetaData) {
        return mappedMetaData.getMappedMethod().toString().contains(EXPAND_STATEMENT_METHOD);
    }

}
