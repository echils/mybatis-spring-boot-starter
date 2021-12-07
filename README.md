# Mybatis Spring Boot Starter Quickstart Guide

This README.md describes how to quickly configure and use the starter.


<p align="center">
  <a>
   <img alt="Framework" src="ECHILS.PNG">
  </a>
</p>

## Development Environment  
JDK     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1.8.0_202  
Maven   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3.5.4  
Spring Boot &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.3.4.RELEASE  

## Functional Description
This starter can provide common MyBatis functions for developers by default without introducing MyBatis -Generator, and supports method name query functions like JPA, minimize the number of XML writing scenarios which greatly improves development efficiency.


## Quick Start Example  

##### 1、Add the dependency to the pom.xml.  
````
<dependency>
    <groupId>com.github.echils</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
````
##### 2、Extend from [SpecificationMapper](./src/main/java/com/github/mybatis/specification/SpecificationMapper.java) or [DynamicMapper](./src/main/java/com/github/mybatis/specification/DynamicMapper.java).Using the user table as an example, extend from SpecificationMapper will automatically register CURD interfaces, and then I define some interfaces to query by method
````
package com.github.echils.mybatis.model;

import com.github.mybatis.annotations.Column;
import com.github.mybatis.annotations.Table;
import com.github.mybatis.annotations.Where;
import lombok.Data;

import java.util.Date;

/**
 * 用户表对应的实体类
 *
 * @Table  可以主动指定表名，如果不指定将会使用默认数据库解析策略解析，当然也可以通过实现接口{@link TableNameResolver}自定义解析规则
 * @Column 可以主动指定字段名，如果不指定将会使用默认数据库解析策略解析，当然也可以通过实现接口{@link ColumnNameResolver}自定义解析规则
 *         同时通过该注解可以配置新增时默认值,支持数据库函数。
 * @Where  指定全局默认查询条件，类型JPA
 *
 *
 * @author echils
 */
@Data
@Table(value = "`user`")
@Where(clause = "logical=true")
public class User {

    /**
     * 唯一标识
     */
    private String id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private int age;

    /**
     * 性别
     */
    private String sex;

    /**
     * 企业邮箱（数据库无此字段，解析时将会自动排除）
     */
    private String enterpriseMail;

    /**
     * 手机号（数据库无此字段，解析时将会自动排除）
     */
    private String telephone;

    /**
     * 创建时间
     */
    @Column(value = "create_time", defaultValue = "NOW()")
    private Date createTime;

    /**
     * 逻辑列
     */
    @Column(defaultValue = "true")
    private Boolean logical;

}

````



````
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
 * 用户表有主键，有增删改查的需求故继承SpecificationMapper，如果只是一个视图则继承DynamicMapper即可
 * 自定义方法命名查询和JPA用法相同，有过JPA使用经验的开发者可以无缝切换。
 * 如果表是联合主键，那么SpecificationMapper的ID也指定为实体类即可，如 UserMapper extends SpecificationMapper<User, User>
 * 
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
    UserView findByAgeAndSex(int age, String sex);

    /**
     * 自定义方法：通过年龄段查询
     * 将使用{@link DynamicMethodStatementLoader}动态解析方法名来注册Statement
     */
    Set<User> findByAgeBetween(int minAge, int maxAge);

    /**
     * 自定义方法：通过年龄段查询
     * 将使用{@link DynamicMethodStatementLoader}动态解析方法名来注册Statement
     */
    List<User> findByNameIn(List<String> names);

}


````
##### 3、Use the mapper as usual in the project

## More Usage Samples
Demo:&nbsp;&nbsp;[quick-start-sample](mybatis-quickstart-sample) 