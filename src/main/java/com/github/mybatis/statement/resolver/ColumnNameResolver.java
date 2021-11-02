package com.github.mybatis.statement.resolver;

import java.lang.reflect.Field;

/**
 * 列名解析器
 *
 * @author echils
 */
public interface ColumnNameResolver {

    /**
     * 更具属性解析数据库列名
     *
     * @param field 实体类属性
     */
    String resolveTableName(Field field);

}
