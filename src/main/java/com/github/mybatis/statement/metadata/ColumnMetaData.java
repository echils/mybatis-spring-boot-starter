package com.github.mybatis.statement.metadata;

import lombok.Data;
import org.apache.ibatis.type.JdbcType;

/**
 * 列元数据信息
 *
 * @author echils
 */
@Data
public class ColumnMetaData implements Cloneable {

    /**
     * 数据库字段名称
     */
    private String columnName;

    /**
     * 实体类字段名称
     */
    private String fieldName;

    /**
     * Jdbc类型
     */
    private JdbcType jdbcType;

    /**
     * Java类型
     */
    private Class<?> javaType;

    /**
     * 是否是主键
     */
    private boolean primaryKey;

    /**
     * 是否允许更新
     */
    private boolean updatable = true;

    /**
     * 是否是逻辑列
     */
    private boolean logical;

    /**
     * 逻辑存在值
     */
    private String existValue;

    /**
     * 逻辑删除值
     */
    private String deleteValue;

    /**
     * 新增默认值
     */
    private String defaultInsertValue;

    /**
     * 更新默认值
     */
    private String defaultUpdateValue;


    @Override
    protected ColumnMetaData clone() throws CloneNotSupportedException {
        return (ColumnMetaData) super.clone();
    }

}
