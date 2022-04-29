package com.github.echils.mybatis.model;

import com.github.mybatis.annotations.Column;
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
    private Integer age;

    /**
     * 性别
     */
    private String sex;

    /**
     * 企业邮箱（数据库无此字段）
     */
    private String enterpriseMail;

    /**
     * 手机号（数据库无此字段）
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
