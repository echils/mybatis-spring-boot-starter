package com.github.mybatis.specification;

import java.util.Collection;
import java.util.List;

/**
 * 用于对应数据库中包含主键的实体类
 * 如果用户业务映射器继承该接口，那么将自动为用户提供增删改查等规范的常用功能
 *
 * @param <T>  表对应的类
 * @param <ID> 表主键对应的类型
 * @author echils
 */
public interface SpecificationMapper<ID, T> extends DynamicMapper<T> {


    /**
     * 通过主键查询
     *
     * @param key 主键
     */
    T selectByPrimaryKey(ID key);


    /**
     * 通过主键判断数据是否存在
     *
     * @param key 主键
     */
    boolean existByPrimaryKey(ID key);


    /**
     * 新增
     *
     * @param entity 实体类
     */
    int insert(T entity);


    /**
     * 选择性新增：只插入不为空的字段
     *
     * @param entity 实体类
     */
    int insertSelective(T entity);


    /**
     * 批量插入
     *
     * @param entities 实体类集合
     */
    int insertBatch(Collection<T> entities);


    /**
     * 通过主键删除
     *
     * @param key 主键
     */
    int deleteByPrimaryKey(ID key);


    /**
     * 通过主键批量删除
     *
     * @param keys 主键集合
     */
    int deleteByPrimaryKeys(Collection<ID> keys);


    /**
     * 更新
     *
     * @param entity 实体类
     */
    int update(T entity);


    /**
     * 选择性更新：只更新非空字段
     *
     * @param entity 实体类
     */
    int updateSelective(T entity);


    /**
     * 批量更新
     *
     * @param entities 实体类集合
     */
    int updateBatch(Collection<T> entities);


    /**
     * 批量更新： 对主键集合所匹配的数据（以实体类非空字段为例）进行更新
     *
     * @param keys   主键集合
     * @param entity 实体类
     */
    int updateSelectiveBatch(Collection<ID> keys, T entity);


    /**
     * 不存在就新增，存在就更新 (效果和JPA一样，某些时候很方便)
     *
     * @param entity 实体类
     */
    int save(T entity);


    /**
     * 批量新增或更新,不存在就新增，存在就更新 (效果和JPA一样，某些时候很方便)
     *
     * @param entities 实体类集合
     */
    int saveAll(Collection<T> entities);


    /**
     * 统计总数
     */
    default int total() { return count(null); }


    /**
     * 查询所有数据
     */
    default List<T> findAll() { return findAll(null); }



}
