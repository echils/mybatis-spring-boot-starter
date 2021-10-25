package com.github.mybatis.statement.metadata;

import lombok.Data;
import org.apache.ibatis.session.Configuration;

/**
 * 实体类元数据
 *
 * @author echils
 */
@Data
public class EntityMetaData {

    /**
     * 实体类信息
     */
    private Class<?> entityClass;

    /**
     * 表元数据信息
     */
    private TableMetaData tableMetaData;

    /**
     * 通用查询条件,通过{@link com.github.mybatis.annotations.Where}指定
     */
    private String whereClause;

    /**
     * 逻辑删除语句,通过{@link com.github.mybatis.annotations.LogicalDelete}指定
     */
    private String logicalDeleteClause;

    /**
     * Mybatis核心配置类
     */
    private Configuration configuration;

}
