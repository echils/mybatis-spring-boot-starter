package com.github.mybatis.statement.resolver;

/**
 * 表明解析器
 *
 * @author echils
 */
public interface TableNameResolver {

    /**
     * 根据实体类解析数据库表明
     *
     * @param tableEntityClazz 映射器绑定的实体类
     */
    String resolveTableName(Class<?> tableEntityClazz);

}
