package com.github.mybatis.specification;

import com.github.mybatis.specification.condition.DynamicParam;

import java.util.List;

/**
 * 如果用户业务映射器继承该接口，那么将自动为用户提供动态查询的功能
 *
 * @author echils
 */
public interface DynamicMapper<T> extends ExpandMapper<T> {


    /**
     * 通过动态参数进行统计
     *
     * @param dynamicParam 自定义参数
     */
    int count(DynamicParam dynamicParam);


    /**
     * 通过动态参数进行查询
     *
     * @param dynamicParam 自定义参数
     */
    List<T> findAll(DynamicParam dynamicParam);


}
