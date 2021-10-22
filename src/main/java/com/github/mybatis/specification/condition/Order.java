package com.github.mybatis.specification.condition;

/**
 * 排序参数
 *
 * @author echils
 */
public class Order {

    /**
     * 排序属性
     */
    private String key;

    /**
     * 排序条件
     */
    private Rule rule;

    public enum Rule {
        ASC, DESC
    }


}
