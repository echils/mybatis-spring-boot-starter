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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.github.mybatis.MybatisExpandContext.*;

/**
 * 插入功能加载器
 *
 * @author echils
 */
@Slf4j
public class InsertStatementLoader extends AbstractExpandStatementLoader {

    /**
     * 拓展方法名
     */
    private static final String EXPAND_STATEMENT_METHOD
            = SpecificationMapper.class.getName() + ".insert";

    @Override
    SqlCommandType sqlCommandType() {
        return SqlCommandType.INSERT;
    }

    @Override
    SqlSource sqlSourceBuild(MappedMetaData mappedMetaData) {

        Configuration configuration =
                mappedMetaData.getMapperFactoryBean().getSqlSession().getConfiguration();
        TableMetaData tableMetaData = mappedMetaData.getTableMetaData();
        List<SqlNode> sqlNodes = new LinkedList<>();
        List<SqlNode> columnSqlNodes = new LinkedList<>();
        List<SqlNode> paramSqlNodes = new LinkedList<>();

        for (ColumnMetaData columnMetaData : tableMetaData.getColumnMetaDataList()) {
            StaticTextSqlNode paramSqlNode = new StaticTextSqlNode(String.format(MYBATIS_PARAM_EXPRESSION,
                    columnMetaData.getFieldName(), columnMetaData.getJdbcType()) + ",");
            String defaultValue = columnMetaData.isLogical() ?
                    columnMetaData.getExistValue() : columnMetaData.getDefaultInsertValue();
            paramSqlNodes.add(StringUtils.isNotBlank(defaultValue) ?
                    new ChooseSqlNode(Collections.singletonList(new IfSqlNode(paramSqlNode,
                            String.format(MYBATIS_TEST_EXPRESSION, columnMetaData.getFieldName()))),
                            new StaticTextSqlNode(defaultValue + ",")) : paramSqlNode);
            columnSqlNodes.add(new StaticTextSqlNode(KEYWORDS_ESCAPE_FUNCTION.apply(columnMetaData.getColumnName()) + ","));
        }

        sqlNodes.add(new StaticTextSqlNode("INSERT INTO " +
                KEYWORDS_ESCAPE_FUNCTION.apply(tableMetaData.getName())));
        sqlNodes.add(new TrimSqlNode(configuration,
                new MixedSqlNode(columnSqlNodes), " (", null,
                ") ", ","));
        sqlNodes.add(new TrimSqlNode(configuration,
                new MixedSqlNode(paramSqlNodes), " VALUES (", null,
                ")", ","));

        return new DynamicSqlSource(configuration, new MixedSqlNode(sqlNodes));
    }

    @Override
    public boolean match(MappedMetaData mappedMetaData) {
        return mappedMetaData.getMappedMethod().toString().contains(EXPAND_STATEMENT_METHOD);
    }

}
