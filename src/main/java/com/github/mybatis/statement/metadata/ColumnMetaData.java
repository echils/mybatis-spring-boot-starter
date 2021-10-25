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
     * Jdbc类型
     */
    private JdbcType jdbcType;


}
