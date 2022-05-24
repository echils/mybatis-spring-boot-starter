package com.github.mybatis.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.github.mybatis.MybatisExpandContext.UNDEFINED_LABEL;
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


    /**
     * 逻辑字段
     */
    String logicalField() default UNDEFINED_LABEL;


    /**
     * 逻辑存在值，默认0
     */
    String existValue() default "0";


    /**
     * 逻辑删除值，默认0
     */
    String deleteValue() default "1";


}
