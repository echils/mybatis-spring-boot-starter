package com.github.echils.mybatis.model;

import lombok.Data;

import java.util.Date;

/**
 * 用户视图
 *
 * @author echils
 */
@Data
public class UserView {

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
     * 部门
     */
    private String department;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 逻辑列
     */
    private boolean logical;

}
