package com.github.mybatis.specification.condition;

import lombok.Data;

/**
 * 动态查询参数
 *
 * @author echils
 */
@Data
public class Condition {

    /**
     *
     */
    private String key;

    /**
     * 比较规则
     */
    private Rule rule;

    /**
     * 值
     */
    private Object value;


    /**
     * 条件
     */
    public enum Rule {

        EQ("="),
        NOT_EQ("!="),
        LT("<"),
        GT(">"),
        LE("<="),
        GE(">="),
        NOT_LIKE("NOT LIKE"),
        LIKE("LIKE"),
        LIKE_LEFT("LIKE"),
        LIKE_RIGHT("LIKE"),
        IN("IN"),
        NOT_IN("NOT IN"),
        IS_NULL("IS NULL"),
        IS_NOT_NULL("IS NOT NULL"),
        IS_BLANK("= ''"),
        IS_NOT_BLANK("!= ''"),
        BETWEEN("BETWEEN"),
        NOT_BETWEEN("NOT BETWEEN");

        public final String expression;

        Rule(String expression) {
            this.expression = expression;
        }
    }

}
