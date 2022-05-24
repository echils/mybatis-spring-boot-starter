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
     * 指定对应的数据库列名,缺省时使用{@link com.github.mybatis.statement.resolver.DefaultColumnNameResolver}进行字段自动解析映射
     */
    String value() default UNDEFINED_LABEL;


    /**
     * 标记对应的列是否允许更新操作
     */
    boolean updatable() default true;


    /**
     * 新增时默认值（当且仅当新增操作时有效）
     * 默认值生效的前提实体类属性没有自定义赋值并且默认值不为【UNDEFINED】
     * 默认值除基本数据类型外，也可以使用数据库函数
     */
    String defaultInsertValue() default UNDEFINED_LABEL;


    /**
     * 更新时默认值（当且仅当更新操作时有效）
     * 默认值生效的前提实体类属性没有自定义赋值并且默认值不为【UNDEFINED】
     * 默认值除基本数据类型外，也可以使用数据库函数
     */
    String defaultUpdateValue() default UNDEFINED_LABEL;


}
