package com.github.mybatis.specification.condition;

import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
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
     * Where条件,多条件默认用{@link Joint#AND}连接，可自定义指定
     */
    private LinkedHashMap<Condition, Joint> whereConditions = new LinkedHashMap<>();

    /**
     * Group条件
     */
    private List<String> groupConditions = new ArrayList<>();

    /**
     * Having条件，多条件默认用{@link Joint#AND}连接，可自定义指定
     */
    private LinkedHashMap<Condition, Joint> havingConditions = new LinkedHashMap<>();

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
        return this.where(condition, Joint.AND);
    }

    /**
     * 添加Where条件
     */
    public DynamicParam where(Condition condition, Joint joint) {
        if (condition != null) {
            whereConditions.put(condition, joint);
        }
        return this;
    }

    /**
     * 添加Where条件
     */
    public DynamicParam where(Consumer<Condition> condition) {
        return this.where(condition, Joint.AND);
    }

    /**
     * 添加Where条件
     */
    public DynamicParam where(Consumer<Condition> condition, Joint joint) {
        if (condition != null) {
            Condition body = new Condition();
            condition.accept(body);
            return this.where(body, joint);
        }
        return this;
    }

    /**
     * 添加Group条件
     */
    public DynamicParam groupBy(String... params) {
        groupConditions.addAll(Arrays.asList(params));
        return this;
    }

    /**
     * 添加Having条件
     */
    public DynamicParam having(Condition condition) {
        return this.having(condition, Joint.AND);
    }

    /**
     * 添加Having条件
     */
    public DynamicParam having(Condition condition, Joint joint) {
        if (condition != null) {
            havingConditions.put(condition, joint);
        }
        return this;
    }

    /**
     * 添加Having条件
     */
    public DynamicParam having(Consumer<Condition> condition) {
        return this.having(condition, Joint.AND);
    }

    /**
     * 添加Having条件
     */
    public DynamicParam having(Consumer<Condition> condition, Joint joint) {
        if (condition != null) {
            Condition body = new Condition();
            condition.accept(body);
            return this.having(body, joint);
        }
        return this;
    }

    /**
     * 添加Order条件
     */
    public DynamicParam order(Order order) {
        if (order != null) {
            orderConditions.add(order);
        }
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
        return order(new Order(param, rule));
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
            if (pageIndex < 1) {
                pageIndex = 1;
            }
            limit = new Limit((pageIndex - 1) * pageSize, pageSize);
        }
        return this;
    }

    /**
     * 将最后一个Where连接条件置为空
     */
    public Map<Condition, Joint> getWhereConditions() {
        if (MapUtils.isNotEmpty(whereConditions)) {
            Condition[] conditions = whereConditions.keySet().toArray(new Condition[whereConditions.size()]);
            whereConditions.replace(conditions[conditions.length - 1], null);
        }
        return whereConditions;
    }

    /**
     * 将最后一个Having连接条件置为空
     */
    public LinkedHashMap<Condition, Joint> getHavingConditions() {
        if (MapUtils.isNotEmpty(havingConditions)) {
            Condition[] conditions = havingConditions.keySet().toArray(new Condition[havingConditions.size()]);
            havingConditions.replace(conditions[conditions.length - 1], null);
        }
        return havingConditions;
    }

    /**
     * 多条件连接关键字
     */
    public enum Joint {
        AND, OR
    }

}
