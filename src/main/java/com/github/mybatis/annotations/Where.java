package com.github.mybatis.annotations;

import java.lang.annotation.*;

/**
 * 实体类注解，用于自定义通用查询条件（类似JPA）
 * 使用该注解后，将在拓展功能查询条件的基础上统一追加此条件
 *
 * @author echils
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Where {

    /**
     * 查询条件
     */
    String clause() default "";

}
