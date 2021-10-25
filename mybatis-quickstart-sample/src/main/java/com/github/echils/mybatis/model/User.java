package com.github.echils.mybatis.model;

import com.github.mybatis.annotations.Column;
import com.github.mybatis.annotations.LogicalDelete;
import com.github.mybatis.annotations.Table;
import com.github.mybatis.annotations.Where;
import lombok.Data;

import java.util.Date;

/**
 * 用户表
 *
 * @author echils
 */
@Data
@Table(value = "user")
@Where(clause = "logical=true")
@LogicalDelete(clause = "update set logical = false")
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
     * 创建时间
     */
    @Column(value = "create_time", nullable = false)
    private Date createTime;

    /**
     * 逻辑列
     */
    private boolean logical;

}
