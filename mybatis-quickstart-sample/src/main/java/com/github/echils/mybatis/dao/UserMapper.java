package com.github.echils.mybatis.dao;

import com.github.echils.mybatis.model.User;
import com.github.mybatis.specification.SpecificationMapper;
import com.github.mybatis.statement.loader.DynamicMethodStatementLoader;

/**
 * UserMapper
 *
 * @author echils
 */
public interface UserMapper extends SpecificationMapper<String, User> {

    /**
     * 自定义方法：通过用户名称查找
     * 将使用{@link DynamicMethodStatementLoader}动态解析方法名来注册Statement
     */
    User findByName(String name);

}

