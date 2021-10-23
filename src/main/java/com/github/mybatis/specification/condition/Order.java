package com.github.mybatis.specification.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 排序参数
 *
 * @author echils
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
