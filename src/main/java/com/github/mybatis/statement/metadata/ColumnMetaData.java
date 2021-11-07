package com.github.mybatis.statement.metadata;

import lombok.Data;
import org.apache.ibatis.type.JdbcType;

/**
 * 列元数据信息
 *
 * @author echils
 */
@Data
public class ColumnMetaData {

    /**
     * 数据库字段名称
     */
    private String columnName;

    /**
     * 实体类字段名称
     */
    private String fieldName;

    /**
     * 是否是主键
     */
    private boolean primaryKey;

    /**
     * 是否可以为空
     */
    private boolean nullable = true;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * Jdbc类型
     */
    private JdbcType jdbcType;

    /**
     * Java类型
     */
    private Class<?> javaType;


    public boolean isNullable() {
        return !primaryKey && nullable;
    }


}
