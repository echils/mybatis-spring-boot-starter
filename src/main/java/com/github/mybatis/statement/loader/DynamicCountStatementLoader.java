package com.github.mybatis.statement.loader;

import com.github.mybatis.specification.DynamicMapper;
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

/**
 * 动态统计功能加载器
 *
 * @author echils
 */
@Slf4j
public class DynamicCountStatementLoader extends AbstractExpandStatementLoader {

    /**
     * 拓展方法名
     */
    private static final String EXPAND_STATEMENT_METHOD
            = DynamicMapper.class.getName() + ".count";

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

        sqlNodes.add(new StaticTextSqlNode("SELECT COUNT(0) FROM "
                + KEYWORDS_ESCAPE_FUNCTION.apply(tableMetaData.getName()) + " WHERE 1=1"));
        if (StringUtils.isNotBlank(mappedMetaData.getWhereClause())) {
            sqlNodes.add(new StaticTextSqlNode(" AND " + mappedMetaData.getWhereClause()));
        }

        //DynamicParam#whereConditions属性
        sqlNodes.add(new IfSqlNode(new MixedSqlNode(Arrays.asList(new StaticTextSqlNode(" AND "),
                new IfSqlNode(new MixedSqlNode(Collections.singletonList(
                        new ForEachSqlNode(configuration, new MixedSqlNode(Arrays.asList(
                                new TextSqlNode(" ${key.param} ${key.rule.value} "),
                                new ChooseSqlNode(Arrays.asList(
                                        new IfSqlNode(new ForEachSqlNode(configuration, new StaticTextSqlNode(" #{data} "),
                                                "key.value", null, "data",
                                                "(", ")", ","), "key.rule.name == 'IN' || key.rule.name == 'NOT_IN'"),
                                        new IfSqlNode(new StaticTextSqlNode(""), " key.rule.name()=='IS_BLANK' " +
                                                "||key.rule.name()=='IS_NOT_BLANK' || key.rule.name()=='IS_NULL' " +
                                                "|| key.rule.name()=='IS_NOT_NULL'"),
                                        new IfSqlNode(new TextSqlNode("${key.value}"), "key.rule.name =='BETWEEN' || key.rule.name =='NOT_BETWEEN'")),
                                        new StaticTextSqlNode("#{key.value}")),
                                new ChooseSqlNode(Collections.singletonList(new IfSqlNode(new TextSqlNode("${value}"),
                                        "value != null")), new StaticTextSqlNode(""))
                        )), "whereConditions", "key", "value", null,
                                null, null))), "whereConditions" + " != null"))),
                "whereConditions != null && whereConditions.size() > 0"));

        return new DynamicSqlSource(configuration, new MixedSqlNode(sqlNodes));
    }

    @Override
    public boolean match(MappedMetaData mappedMetaData) {
        return mappedMetaData.getMappedMethod().toString().contains(EXPAND_STATEMENT_METHOD);
    }


}
