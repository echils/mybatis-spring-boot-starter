package com.github.mybatis.statement.loader;

import com.github.mybatis.specification.SpecificationMapper;
import com.github.mybatis.statement.metadata.ColumnMetaData;
import com.github.mybatis.statement.metadata.MappedMetaData;
import com.github.mybatis.statement.metadata.TableMetaData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

import java.util.LinkedList;
import java.util.List;

import static com.github.mybatis.MybatisExpandContext.KEYWORDS_ESCAPE_FUNCTION;

/**
 * 通过主键查询功能加载器
 *
 * @author echils
 */
@Slf4j
public class SelectByPrimaryKeyStatementLoader extends AbstractExpandStatementLoader {

    /**
     * 拓展方法名
     */
    private static final String EXPAND_STATEMENT_METHOD
            = SpecificationMapper.class.getName() + ".selectByPrimaryKey";

    @Override
    SqlCommandType sqlCommandType() {
        return SqlCommandType.SELECT;
    }

    @Override
    SqlSource sqlSourceBuild(MappedMetaData mappedMetaData) {

        Configuration configuration =
                mappedMetaData.getMapperFactoryBean().getSqlSession().getConfiguration();
        TableMetaData tableMetaData = mappedMetaData.getTableMetaData();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT ");
        sqlBuilder.append(mappedMetaData.getBaseColumnList());
        sqlBuilder.append(" FROM ");
        sqlBuilder.append(KEYWORDS_ESCAPE_FUNCTION.apply(tableMetaData.getName()));
        sqlBuilder.append(" WHERE 1=1");
        if (StringUtils.isNotBlank(mappedMetaData.getWhereClause())) {
            sqlBuilder.append(" AND ").append(mappedMetaData.getWhereClause());
        }

        List<ColumnMetaData> columnMetaDataList = tableMetaData.getColumnMetaDataList();
        columnMetaDataList.stream().filter(ColumnMetaData::isLogical).forEach(columnMetaData ->
                sqlBuilder.append(" AND ").append(columnMetaData.getColumnName()).append("=").append(columnMetaData.getExistValue()));
        List<ParameterMapping> parameterMappings = new LinkedList<>();
        columnMetaDataList.stream()
                .filter(ColumnMetaData::isPrimaryKey).forEach(columnMetaData -> {
            sqlBuilder.append(" AND ").append(KEYWORDS_ESCAPE_FUNCTION
                    .apply(columnMetaData.getColumnName())).append(" = ?");
            parameterMappings.add(new ParameterMapping.Builder(configuration, columnMetaData.getFieldName(),
                    columnMetaData.getJavaType()).jdbcType(columnMetaData.getJdbcType()).build());
        });

        return new StaticSqlSource(configuration, sqlBuilder.toString(), parameterMappings);
    }

    @Override
    public boolean match(MappedMetaData mappedMetaData) {
        return mappedMetaData.getMappedMethod().toString().contains(EXPAND_STATEMENT_METHOD);
    }
}
