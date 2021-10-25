package com.github.mybatis.annotations;

import java.lang.annotation.*;
import java.util.Collection;

/**
 * 逻辑删除注解，当实体类追加这个注解时，拓展功能
 * {@link com.github.mybatis.specification.SpecificationMapper#deleteByPrimaryKey(Object)}
 * {@link com.github.mybatis.specification.SpecificationMapper#deleteByPrimaryKeys(Collection)}
 * 将从原本的物理删除替换为逻辑删除
 *
 * @author echils
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogicalDelete {

    /**
     * 逻辑删除语句
     */
    String clause();


}
