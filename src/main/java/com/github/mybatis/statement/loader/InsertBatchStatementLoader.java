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
import static com.github.mybatis.MybatisExpandContext.COLUMN_ESCAPE_FUNCTION;

/**
 * 批量插入功能加载器
 *
 * @author echils
 */
@Slf4j
public class InsertBatchStatementLoader extends AbstractExpandStatementLoader {

    /**
     * 拓展方法名
     */
    private static final String EXPAND_STATEMENT_METHOD
            = SpecificationMapper.class.getName() + ".insertBatch";

    @Override
    SqlCommandType sqlCommandType() {
        return SqlCommandType.INSERT;
    }

    @Override
    SqlSource sqlSourceBuild(MappedMetaData mappedMetaData) {

        Configuration configuration =
                mappedMetaData.getMapperFactoryBean().getSqlSession().getConfiguration();
        List<SqlNode> sqlNodes = new LinkedList<>();
        List<SqlNode> columnSqlNodes = new LinkedList<>();
        List<SqlNode> paramSqlNodes = new LinkedList<>();
        TableMetaData tableMetaData = mappedMetaData.getTableMetaData();

        for (ColumnMetaData columnMetaData : tableMetaData.getColumnMetaDataList()) {
            StaticTextSqlNode paramSqlNode = new StaticTextSqlNode(String.format(MYBATIS_PARAM_EXPRESSION,
                    MYBATIS_FOREACH_PARAM + "." + columnMetaData.getFieldName(), columnMetaData.getJdbcType()) + ",");
            paramSqlNodes.add(StringUtils.isNotBlank(columnMetaData.getDefaultValue()) ?
                    new ChooseSqlNode(Collections.singletonList(new IfSqlNode(paramSqlNode,
                            String.format(MYBATIS_TEST_EXPRESSION, MYBATIS_FOREACH_PARAM + "." + columnMetaData.getFieldName()))),
                            new StaticTextSqlNode(columnMetaData.getDefaultValue() + ",")) : paramSqlNode);
            columnSqlNodes.add(new StaticTextSqlNode(COLUMN_ESCAPE_FUNCTION.apply(columnMetaData.getColumnName()) + ","));
        }

        sqlNodes.add(new StaticTextSqlNode("INSERT INTO " +
                COLUMN_ESCAPE_FUNCTION.apply(tableMetaData.getName())));
        sqlNodes.add(new TrimSqlNode(configuration , new MixedSqlNode(columnSqlNodes) ," (" ,
                null, ") " ,","));
        sqlNodes.add(new StaticTextSqlNode(" VALUES "));
        sqlNodes.add(new ForEachSqlNode(configuration ,
                new TrimSqlNode(configuration ,new MixedSqlNode(paramSqlNodes) ,"(" , null ,
                        ")" ,",") , "collection" ,null ,
                MYBATIS_FOREACH_PARAM ,null ,null ,","));
        return new DynamicSqlSource(configuration, new MixedSqlNode(sqlNodes));
    }

    @Override
    public boolean match(MappedMetaData mappedMetaData) {
        return mappedMetaData.getMappedMethod().toString().contains(EXPAND_STATEMENT_METHOD);
    }

}
