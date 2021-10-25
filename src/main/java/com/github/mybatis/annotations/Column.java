package com.github.mybatis.annotations;

import java.lang.annotation.*;

/**
 * 实体类属性注解，用于手动指定列属性参数，优先级高于自动解析
 *
 * @author echils
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {


    /**
     * 指定对应的数据库列名,缺省时使用{}进行字段自动解析映射
     */
    String value();


    /**
     * 是否可以为空
     */
    boolean nullable() default true;


}
