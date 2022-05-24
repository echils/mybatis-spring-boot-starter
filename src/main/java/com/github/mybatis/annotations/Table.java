package com.github.mybatis.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 实体类注解，用于手动指定数据库表名称，优先级高于自动解析
 *
 * @author echils
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface Table {

    /**
     * 指定对应的数据库表名,缺省时使用{@link com.github.mybatis.statement.resolver.DefaultTableNameResolver}进行解析映射
     */
    String value();

}
