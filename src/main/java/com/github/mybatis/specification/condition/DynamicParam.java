package com.github.mybatis.specification.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.github.mybatis.MybatisExpandContext.COLUMN_ESCAPE_FUNCTION;

/**
 * 使用动态拓展功能时使用的动态参数
 * 支持动态条件过滤、分组、分组过滤、排序、分页
 *
 * @author echils
 */
public class DynamicParam {

    /**
     * Where条件，多个条件之间将用AND连接
     */
    private List<Condition> whereConditions = new ArrayList<>();

    /**
     * Group条件
     */
    private List<String> groupConditions = new ArrayList<>();

    /**
     * Having条件，多个条件之间将用AND连接
     */
    private List<Condition> havingConditions = new ArrayList<>();

    /**
     * Order条件
     */
    private List<Order> orderConditions = new ArrayList<>();

    /**
     * Limit条件
     */
    private Limit limit;

    /**
     * 添加Where条件
     */
    public DynamicParam where(Condition condition) {
        if (condition != null) { whereConditions.add(condition); }
        return this;
    }

    /**
     * 添加Where条件
     */
    public DynamicParam where(Consumer<Condition> condition){
        if (condition != null) {
            Condition body = new Condition();
            condition.accept(body);
            where(body);
        }
        return this;
    }

    /**
     * 添加Group条件
     */
    public DynamicParam groupBy(String ... keys){
        for (String key : keys) { groupConditions.add(COLUMN_ESCAPE_FUNCTION.apply(key)); }
        return this;
    }

}
