package com.github.mybatis.annotations;

import java.lang.annotation.*;

/**
 * 逻辑注解
 * 用于修饰逻辑列，当一个实体类中有属性被{@link Logical} 修饰时，
 * 其删除操作将自动转换为逻辑删除，查询时也将默认查询逻辑存在数据。
 * 此注解将比全局逻辑配置{@link com.github.mybatis.MybatisExpandProperties}优先级更高
 *
 * @author echils
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Logical {

    /**
     * 逻辑存在值，默认0。
     * 数据新增时优先级大于{@link Column#defaultInsertValue()}
     */
    String existValue() default "0";

    /**
     * 逻辑删除值，默认1
     */
    String deleteValue() default "1";

}
