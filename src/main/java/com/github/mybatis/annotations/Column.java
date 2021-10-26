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
     * 是否可以为空，默认为true
     * 如果为false，当属性没有自定义赋值且没有指定默认值时将抛异常提醒
     */
    boolean nullable() default true;


    /**
     * 默认值
     * 默认值生效的前提实体类属性没有自定义赋值并且默认值不为【UNDEFINED】
     * 默认值除基本数据类型外，也可以使用数据库函数
     */
    String defaultValue() default "UNDEFINED";


}
