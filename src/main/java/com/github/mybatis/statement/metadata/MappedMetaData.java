package com.github.mybatis.statement.metadata;

import com.github.mybatis.specification.DynamicMapper;
import com.github.mybatis.specification.SpecificationMapper;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeanUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.mybatis.MybatisExpandContext.*;

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

    public TableMetaData getTableMetaData() {
        TableMetaData cloneMetaData;
        try {
            cloneMetaData= tableMetaData.clone();
        } catch (CloneNotSupportedException e) {
            TableMetaData newMeta = new TableMetaData();
            newMeta.setName(tableMetaData.getName());
            newMeta.setEntityName(tableMetaData.getEntityName());
            List<ColumnMetaData> cloneColumnMetaDataList = new ArrayList<>();
            for (ColumnMetaData columnMetaData : tableMetaData.getColumnMetaDataList()) {
                try {
                    ColumnMetaData clone = columnMetaData.clone();
                    cloneColumnMetaDataList.add(clone);
                } catch (CloneNotSupportedException e1) {
                    ColumnMetaData newColumn = new ColumnMetaData();
                    BeanUtils.copyProperties(columnMetaData, newColumn);
                    cloneColumnMetaDataList.add(newColumn);
                }
            }
            newMeta.setColumnMetaDataList(cloneColumnMetaDataList);
            cloneMetaData = newMeta;
        }
        return cloneMetaData;
    }

    /**
     * 获取全局查询条件
     */
    public String getWhereClause() {
        if (StringUtils.isNotBlank(whereClause)) {
            String firstClause = whereClause.split(" ")[0];
            if (MYBATIS_WHERE_EXPRESSION.equalsIgnoreCase(firstClause)) {
                whereClause = whereClause.replaceFirst(firstClause, "");
            }
        }
        return whereClause;
    }

    /**
     * 获取Mybatis的StatementId
     */
    public String getMappedStatementId() {
        return mapperInterface.getName() + "." + mappedMethod.getName();
    }


    /**
     * 获取默认查询列
     */
    public String getBaseColumnList() {
        List<ColumnMetaData> columnMetaDataList = tableMetaData.getColumnMetaDataList();
        StringBuilder baseColumnListBuilder = new StringBuilder();
        if (CollectionUtils.isNotEmpty(columnMetaDataList)) {
            for (ColumnMetaData columnMetaData : columnMetaDataList) {
                baseColumnListBuilder.append(KEYWORDS_ESCAPE_FUNCTION
                        .apply(columnMetaData.getColumnName())).append(",");
            }
            baseColumnListBuilder.deleteCharAt(baseColumnListBuilder.lastIndexOf(","));
        }
        return baseColumnListBuilder.toString();
    }


    /**
     * 解析Statement结果集
     */
    public ResultMap getMappedStatementResultMap() {
        Configuration configuration = mapperFactoryBean.getSqlSession().getConfiguration();
        String defaultMappedId = getMappedStatementId() + "-" + EXPAND_DEFAULT_RESULT_MAP;
        Type returnType = mappedMethod.getGenericReturnType();
        Class<?> returnClazzType = null;
        if (returnType instanceof ParameterizedType) {
            ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) returnType;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (ArrayUtils.isNotEmpty(actualTypeArguments)) {
                Optional<Type> optionalType = Arrays.stream(actualTypeArguments)
                        .filter(type -> type instanceof Class).findFirst();
                if (optionalType.isPresent()) {
                    returnClazzType = (Class) optionalType.get();
                }
            }
        } else if (returnType instanceof Class) {
            returnClazzType = (Class) returnType;
            if (returnClazzType.isArray()) {
                returnClazzType = returnClazzType.getComponentType();
            }
        }
        if (returnClazzType != null) {
            if (returnClazzType.isPrimitive()) {
                return new ResultMap.Builder(configuration,
                        defaultMappedId, returnClazzType, Collections.emptyList()).build();
            } else if (entityClass == returnClazzType) {
                return getEntityResultMap(configuration);
            }
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
