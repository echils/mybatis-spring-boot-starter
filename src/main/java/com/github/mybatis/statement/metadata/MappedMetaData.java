package com.github.mybatis.statement.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.MapperFactoryBean;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.mybatis.MybatisExpandContext.EXPAND_DEFAULT_RESULT_MAP;

/**
 * 拓展功能映射元数据
 *
 * @author echils
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MappedMetaData {

    /**
     * 实体类信息
     */
    private Class<?> entityClass;

    /**
     * 映射器类信息
     */
    private Class<?> mapperInterface;

    /**
     * 映射器方法
     */
    private Method mappedMethod;

    /**
     * 表元数据信息
     */
    private TableMetaData tableMetaData;

    /**
     * 统一查询条件,通过{@link com.github.mybatis.annotations.Where}指定
     */
    private String whereClause;

    /**
     * 原生映射器
     */
    private MapperFactoryBean<?> mapperFactoryBean;

    /**
     * 获取Mybatis的StatementId
     */
    public String getMappedStatementId() {
        return mapperInterface.getName() + "." + mappedMethod.getName();
    }

    /**
     * 解析Statement结果集
     */
    public ResultMap getMappedStatementResultMap() {

        Type genericReturnType = mappedMethod.getGenericReturnType();

        String resultMapId = mapperInterface.getName() + "." + EXPAND_DEFAULT_RESULT_MAP;
        Configuration configuration = mapperFactoryBean.getSqlSession().getConfiguration();
        if (configuration.hasResultMap(resultMapId)) {
            return configuration.getResultMap(resultMapId);
        }

        List<ResultMapping> resultMappingList = tableMetaData.getColumnMetaDataList()
                .stream().map(columnMetaData -> {
                    List<ResultFlag> flags = columnMetaData.isPrimaryKey() ?
                            Collections.singletonList(ResultFlag.ID) : new ArrayList<>();
                    return new ResultMapping.Builder(configuration,
                            columnMetaData.getFieldName(), columnMetaData.getColumnName(),
                            columnMetaData.getJavaType())
                            .jdbcType(columnMetaData.getJdbcType()).flags(flags).build();
                }).collect(Collectors.toList());

        ResultMap resultMap = new ResultMap
                .Builder(configuration, resultMapId, entityClass, resultMappingList).build();
        configuration.addResultMap(resultMap);
        return resultMap;
    }

}
