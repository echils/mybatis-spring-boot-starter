package com.github.mybatis.annotations;

import java.lang.annotation.*;

import static com.github.mybatis.MybatisExpandContext.UNDEFINED_LABEL;

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
    String value() default UNDEFINED_LABEL;


    /**
     * 默认值（新增时有效）
     * 默认值生效的前提实体类属性没有自定义赋值并且默认值不为【UNDEFINED】
     * 默认值除基本数据类型外，也可以使用数据库函数
     */
    String defaultValue() default UNDEFINED_LABEL;


}
