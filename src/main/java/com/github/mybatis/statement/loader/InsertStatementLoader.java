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

import static com.github.mybatis.MybatisExpandContext.COLUMN_ESCAPE_FUNCTION;

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
        List<SqlNode> propertySqlNodes = new LinkedList<>();

        for (ColumnMetaData columnMetaData : tableMetaData.getColumnMetaDataList()) {

            StaticTextSqlNode columnSqlNode =
                    new StaticTextSqlNode(COLUMN_ESCAPE_FUNCTION.apply(columnMetaData.getColumnName()));

        }


        sqlNodes.add(new StaticTextSqlNode("INSERT INTO " +
                COLUMN_ESCAPE_FUNCTION.apply(tableMetaData.getName())));
        sqlNodes.add(new TrimSqlNode(configuration,
                new MixedSqlNode(columnSqlNodes), " (", null,
                ") ", ","));
        sqlNodes.add(new TrimSqlNode(configuration,
                new MixedSqlNode(propertySqlNodes), " VALUES (", null,
                ")", ","));
        return new DynamicSqlSource(configuration, new MixedSqlNode(sqlNodes));
    }

    @Override
    public boolean match(MappedMetaData mappedMetaData) {
        return mappedMetaData.getMappedMethod().toString().contains(EXPAND_STATEMENT_METHOD);
    }

}
