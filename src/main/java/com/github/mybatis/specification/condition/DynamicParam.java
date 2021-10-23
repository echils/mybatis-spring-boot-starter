package com.github.mybatis.specification.condition;

import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;


/**
 * 使用动态拓展功能时使用的动态参数
 * 支持动态条件过滤、分组、分组过滤、排序、分页
 *
 * @author echils
 */
@Getter
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
    public DynamicParam groupBy(String ... params){
        groupConditions.addAll(Arrays.asList(params));
        return this;
    }

    /**
     * 添加Having条件
     */
    public DynamicParam having(Condition condition) {
        if (condition != null) { havingConditions.add(condition); }
        return this;
    }

    /**
     * 添加Having条件
     */
    public DynamicParam having(Consumer<Condition> condition) {
        if (condition != null) {
            Condition body = new Condition();
            condition.accept(body);
            having(body);
        }
        return this;
    }

    /**
     * 添加Order条件
     */
    public DynamicParam order(Order order) {
        if (order != null) { orderConditions.add(order); }
        return this;
    }

    /**
     * 添加Order条件
     */
    public DynamicParam order(List<Order> orders) {
        if (CollectionUtils.isNotEmpty(orders)) { orderConditions.addAll(orders); }
        return this;
    }

    /**
     * 添加Order条件
     */
    public DynamicParam order(String param, Order.Rule rule) {
        order(new Order(param, rule));
        return this;
    }

    /**
     * 添加Limit条件
     */
    public DynamicParam limit(Integer size) {
        if (size != null) { limit = new Limit(size); }
        return this;
    }

    /**
     * 添加分页参数
     *
     * @param pageIndex 页索引,下标从1开始
     * @param pageSize  页大小
     */
    public DynamicParam page(Integer pageIndex, Integer pageSize) {
        if (pageIndex != null && pageSize != null) {
            if (pageIndex < 1) { pageIndex = 1; }
            limit = new Limit((pageIndex - 1) * pageSize, pageSize);
        }
        return this;
    }

}
