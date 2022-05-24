package com.github.mybatis.statement.loader;

import com.github.mybatis.specification.SpecificationMapper;
import com.github.mybatis.statement.metadata.ColumnMetaData;
import com.github.mybatis.statement.metadata.MappedMetaData;
import com.github.mybatis.statement.metadata.TableMetaData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.mybatis.MybatisExpandContext.KEYWORDS_ESCAPE_FUNCTION;

/**
 * 通过主键删除功能加载器
 *
 * @author echils
 */
@Slf4j
public class DeleteByPrimaryKeyStatementLoader extends AbstractExpandStatementLoader {

    /**
     * 拓展方法名
     */
    private static final String EXPAND_STATEMENT_METHOD
            = SpecificationMapper.class.getName() + ".deleteByPrimaryKey";

    @Override
    SqlCommandType sqlCommandType() {
        return SqlCommandType.DELETE;
    }

    @Override
    SqlSource sqlSourceBuild(MappedMetaData mappedMetaData) {

        Configuration configuration =
                mappedMetaData.getMapperFactoryBean().getSqlSession().getConfiguration();
        TableMetaData tableMetaData = mappedMetaData.getTableMetaData();
        StringBuilder sqlBuilder = new StringBuilder();
        List<ParameterMapping> parameterMappings = new LinkedList<>();

        List<ColumnMetaData> logicalColumns = tableMetaData.getColumnMetaDataList()
                .stream().filter(ColumnMetaData::isLogical).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(logicalColumns)) {
            sqlBuilder.append("UPDATE ").append(KEYWORDS_ESCAPE_FUNCTION.apply(tableMetaData.getName()));
            logicalColumns.forEach(columnMetaData -> sqlBuilder.append(" SET ")
                    .append(KEYWORDS_ESCAPE_FUNCTION.apply(columnMetaData.getColumnName()))
                    .append(" = ").append(columnMetaData.getDeleteValue()));
        } else {
            sqlBuilder.append("DELETE FROM ");
            sqlBuilder.append(KEYWORDS_ESCAPE_FUNCTION.apply(tableMetaData.getName()));

        }

        sqlBuilder.append(" WHERE 1=1 ");
        if (StringUtils.isNotBlank(mappedMetaData.getWhereClause())) {
            sqlBuilder.append(" AND ").append(mappedMetaData.getWhereClause());
        }

        List<ColumnMetaData> columnMetaDataList = tableMetaData.getColumnMetaDataList();
        columnMetaDataList.stream().filter(ColumnMetaData::isLogical).forEach(columnMetaData ->
                sqlBuilder.append(" AND ").append(columnMetaData.getColumnName()).append("=").append(columnMetaData.getExistValue()));
        columnMetaDataList.stream().filter(ColumnMetaData::isPrimaryKey).forEach(columnMetaData -> {
            parameterMappings.add(new ParameterMapping.Builder(configuration, columnMetaData.getFieldName(),
                    columnMetaData.getJavaType()).jdbcType(columnMetaData.getJdbcType()).build());
            sqlBuilder.append(" AND ").append(KEYWORDS_ESCAPE_FUNCTION
                    .apply(columnMetaData.getColumnName())).append(" = ?");
        });

        return new StaticSqlSource(configuration, sqlBuilder.toString(), parameterMappings);
    }

    @Override
    public boolean match(MappedMetaData mappedMetaData) {
        return mappedMetaData.getMappedMethod().toString().contains(EXPAND_STATEMENT_METHOD);
    }

}
