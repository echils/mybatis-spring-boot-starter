package com.github.mybatis.annotations;

import java.lang.annotation.*;

/**
 * 实体类属性注解
 *
 * @author echils
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {


    /**
     * 指定对应数据库列名,缺省时
     */
    String value() default "";





}
