package com.github.mybatis.statement.loader;

import com.github.mybatis.specification.DynamicMapper;
import com.github.mybatis.statement.metadata.ColumnMetaData;
import com.github.mybatis.statement.metadata.MappedMetaData;
import com.github.mybatis.statement.metadata.TableMetaData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.*;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.mybatis.MybatisExpandContext.KEYWORDS_ESCAPE_FUNCTION;
import static com.github.mybatis.MybatisExpandContext.MYBATIS_COLLECTION_EXPRESSION;

/**
 * 动态查询功能加载器
 *
 * @author echils
 */
@Slf4j
public class DynamicFindAllStatementLoader extends AbstractExpandStatementLoader {

    /**
     * 拓展方法名
     */
    private static final String EXPAND_STATEMENT_METHOD
            = DynamicMapper.class.getName() + ".findAll";

    @Override
    SqlCommandType sqlCommandType() {
        return SqlCommandType.SELECT;
    }

    @Override
    SqlSource sqlSourceBuild(MappedMetaData mappedMetaData) {

        Configuration configuration =
                mappedMetaData.getMapperFactoryBean().getSqlSession().getConfiguration();

        List<SqlNode> sqlNodes = new ArrayList<>();
        TableMetaData tableMetaData = mappedMetaData.getTableMetaData();

        sqlNodes.add(new StaticTextSqlNode("SELECT " + mappedMetaData.getBaseColumnList() + " FROM "
                + KEYWORDS_ESCAPE_FUNCTION.apply(tableMetaData.getName()) + " WHERE 1=1"));
        if (StringUtils.isNotBlank(mappedMetaData.getWhereClause())) {
            sqlNodes.add(new StaticTextSqlNode(" AND " + mappedMetaData.getWhereClause()));
        }
        List<ColumnMetaData> columnMetaDataList = tableMetaData.getColumnMetaDataList();
        columnMetaDataList.stream().filter(ColumnMetaData::isLogical).forEach(columnMetaData ->
                sqlNodes.add(new StaticTextSqlNode(" AND " + columnMetaData.getColumnName() + "=" + columnMetaData.getExistValue())));

        //DynamicParam#whereConditions属性
        sqlNodes.add(new IfSqlNode(new MixedSqlNode(Arrays.asList(new StaticTextSqlNode(" AND "),
                dynamicSqlNode(configuration, "whereConditions"))),
                "whereConditions != null && whereConditions.size() > 0"));

        //DynamicParam#groupConditions属性
        sqlNodes.add(new IfSqlNode(new MixedSqlNode(Arrays.asList(new StaticTextSqlNode(" GROUP BY "),
                new ForEachSqlNode(configuration, new TextSqlNode("${data}"),
                        "groupConditions", null, "data", null, null, ","))),
                "groupConditions != null && groupConditions.size() > 0"));

        //DynamicParam#havingConditions属性
        sqlNodes.add(new IfSqlNode(new MixedSqlNode(Arrays.asList(new StaticTextSqlNode(" HAVING "),
                dynamicSqlNode(configuration, "havingConditions"))),
                "havingConditions != null && havingConditions.size()>0 "));

        //DynamicParam#orderConditions属性
        sqlNodes.add(new IfSqlNode(new ForEachSqlNode(configuration,
                new TextSqlNode(" ${data.key} ${data.rule} "), "orderConditions", null,
                "data", "ORDER BY", null, ","),
                "orderConditions != null and orderConditions.size > 0"));

        //DynamicParam#limit属性
        sqlNodes.add(new IfSqlNode(new StaticTextSqlNode(" LIMIT #{limit.index}, " +
                "#{limit.size} "), "limit != null"));

        return new DynamicSqlSource(configuration, new MixedSqlNode(sqlNodes));
    }

    @Override
    public boolean match(MappedMetaData mappedMetaData) {
        return mappedMetaData.getMappedMethod().toString().contains(EXPAND_STATEMENT_METHOD);
    }

    /**
     * 动态SQL构建
     */
    private IfSqlNode dynamicSqlNode(Configuration configuration, String expression) {
        return new IfSqlNode(new MixedSqlNode(Collections.singletonList(
                new ForEachSqlNode(configuration, new MixedSqlNode(Arrays.asList(
                        new TextSqlNode(" ${key.param} ${key.rule.value} "),
                        new ChooseSqlNode(Arrays.asList(
                                new IfSqlNode(new ChooseSqlNode(Collections.singletonList(new IfSqlNode(
                                        new ForEachSqlNode(configuration, new StaticTextSqlNode(" #{data} "),
                                                "key.value", null, "data",
                                                "(", ")", ","),
                                        "key.value != null && key.value.size > 0")), new StaticTextSqlNode("('')")),
                                        "key.rule.name =='IN' || key.rule.name =='NOT_IN'"),
                                new IfSqlNode(new ForEachSqlNode(configuration, new StaticTextSqlNode(" #{data} "),
                                        "key.value", null, "data",
                                        "(", ")", ","), "key.rule.name =='IN' || key.rule.name =='NOT_IN'"),
                                new IfSqlNode(new StaticTextSqlNode(""), " key.rule.name=='IS_BLANK' " +
                                        "||key.rule.name=='IS_NOT_BLANK' || key.rule.name=='IS_NULL' " +
                                        "|| key.rule.name=='IS_NOT_NULL'"),
                                new IfSqlNode(new TextSqlNode("${key.value}"), "key.rule.name =='BETWEEN' || key.rule.name =='NOT_BETWEEN'")),
                                new StaticTextSqlNode("#{key.value}")),
                        new ChooseSqlNode(Collections.singletonList(new IfSqlNode(new TextSqlNode("${value}"),
                                "value != null")), new StaticTextSqlNode(""))
                )), expression, "key", "value", null, null, null))), String.format(MYBATIS_COLLECTION_EXPRESSION, expression, expression));
    }

}
