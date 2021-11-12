package com.github.mybatis.statement.metadata;

import com.github.mybatis.specification.DynamicMapper;
import com.github.mybatis.specification.SpecificationMapper;
import lombok.Getter;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.MapperFactoryBean;

import java.lang.reflect.Method;
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
@Getter
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
     * 实体类默认结果集
     */
    private ResultMap entityResultMap;


    public MappedMetaData(Class<?> entityClass, Class<?> mapperInterface,
                          Method mappedMethod, TableMetaData tableMetaData,
                          String whereClause, MapperFactoryBean<?> mapperFactoryBean) {

        this.entityClass = entityClass;
        this.mapperInterface = mapperInterface;
        this.mappedMethod = mappedMethod;
        this.tableMetaData = tableMetaData;
        this.whereClause = whereClause;
        this.mapperFactoryBean = mapperFactoryBean;
    }


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
        Configuration configuration = mapperFactoryBean.getSqlSession().getConfiguration();
        Class<?> returnType = mappedMethod.getReturnType();
        String defaultMappedId = getMappedStatementId() + "-" + EXPAND_DEFAULT_RESULT_MAP;
        if (isExpandMethod(mappedMethod)) {
            return returnType.isPrimitive() ? new ResultMap.Builder(configuration,
                    defaultMappedId, returnType, Collections.emptyList()).build() : getEntityResultMap(configuration);
        }
        if (entityClass.isAssignableFrom(returnType)) {
            return getEntityResultMap(configuration);
        } else if (returnType.isArray()) {
            returnType = returnType.getComponentType();
            if (entityClass.isAssignableFrom(returnType)) {
                return getEntityResultMap(configuration);
            }
        }
        if (returnType.isPrimitive()) {
            return new ResultMap.Builder(configuration, defaultMappedId, returnType, Collections.emptyList()).build();
        }
        return new ResultMap.Builder(configuration, defaultMappedId, Object.class, Collections.emptyList()).build();
    }


    /**
     * 获取实体类对应的结果集
     */
    private ResultMap getEntityResultMap(Configuration configuration) {
        if (entityResultMap == null) {
            String resultMapId = mapperInterface.getName() + "." + EXPAND_DEFAULT_RESULT_MAP;
            if (configuration.hasResultMap(resultMapId)) {
                entityResultMap = configuration.getResultMap(resultMapId);
            } else {
                List<ResultMapping> resultMappingList = tableMetaData.getColumnMetaDataList()
                        .stream().map(columnMetaData -> {
                            List<ResultFlag> flags = columnMetaData.isPrimaryKey() ?
                                    Collections.singletonList(ResultFlag.ID) : new ArrayList<>();
                            return new ResultMapping.Builder(configuration,
                                    columnMetaData.getFieldName(), columnMetaData.getColumnName(),
                                    columnMetaData.getJavaType())
                                    .jdbcType(columnMetaData.getJdbcType()).flags(flags).build();
                        }).collect(Collectors.toList());
                entityResultMap = new ResultMap
                        .Builder(configuration, resultMapId, entityClass, resultMappingList).build();
            }
        }
        return entityResultMap;
    }


    /**
     * 是否内置拓展增强方法
     */
    private boolean isExpandMethod(Method method) {
        String methodString = method.toString();
        return methodString.contains(SpecificationMapper.class.getName()) ||
                methodString.contains(DynamicMapper.class.getName());
    }

}
