package com.github.echils.mybatis.dao;

import com.github.echils.mybatis.model.User;
import com.github.echils.mybatis.model.UserView;
import com.github.mybatis.specification.SpecificationMapper;
import com.github.mybatis.statement.loader.DynamicMethodStatementLoader;

import java.util.List;
import java.util.Set;

/**
 * UserMapper
 *
 * @author echils
 */
public interface UserMapper extends SpecificationMapper<String, User> {

    /**
     * 自定义方法：通过用户姓名模糊查找并按注册时间和年龄排序
     * 将使用{@link DynamicMethodStatementLoader}动态解析方法名来注册Statement
     */
    List<UserView> findByNameStartingWithOrderByCreateTimeAscAndAgeDesc(String name);

    /**
     * 自定义方法：通过用年龄和性别查找
     * 将使用{@link DynamicMethodStatementLoader}动态解析方法名来注册Statement
     */
    UserView findByAgeAndSex(Integer age, String sex);

    /**
     * 自定义方法：通过年龄段查询
     * 将使用{@link DynamicMethodStatementLoader}动态解析方法名来注册Statement
     */
    Set<User> findByAgeBetween(Integer minAge, Integer maxAge);

    /**
     * 自定义方法：通过年龄段查询
     * 将使用{@link DynamicMethodStatementLoader}动态解析方法名来注册Statement
     */
    List<User> findByNameIn(List<String> names);

}

