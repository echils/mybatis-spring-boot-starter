package com.github.mybatis.statement.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mybatis.spring.mapper.MapperFactoryBean;

import java.lang.reflect.Method;

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

}
