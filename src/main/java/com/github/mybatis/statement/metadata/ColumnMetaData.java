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
     * 名称
     */
    private String name;

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


    public boolean isNullable() {
        return !primaryKey && nullable;
    }
}
