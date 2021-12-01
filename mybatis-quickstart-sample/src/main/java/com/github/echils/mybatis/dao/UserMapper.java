package com.github.echils.mybatis.dao;

import com.github.echils.mybatis.model.User;
import com.github.mybatis.specification.SpecificationMapper;
import com.github.mybatis.statement.loader.DynamicMethodStatementLoader;

import java.util.List;

/**
 * UserMapper
 *
 * @author echils
 */
public interface UserMapper extends SpecificationMapper<String, User> {

    /**
     * 自定义方法：通过用户姓名查找并按注册时间排序
     * 将使用{@link DynamicMethodStatementLoader}动态解析方法名来注册Statement
     */
    User findByNameOrderByCreateTimeAscAsc(String name);

    /**
     * 自定义方法：通过用年龄和性别查找
     * 将使用{@link DynamicMethodStatementLoader}动态解析方法名来注册Statement
     */
    List<User> findByAgeAndSex(int age, String sex);

    /**
     * 自定义方法：通过用性别或姓名模糊查找
     * 将使用{@link DynamicMethodStatementLoader}动态解析方法名来注册Statement
     */
    User[] findBySexOrNameLike(String sex, String name);

}

