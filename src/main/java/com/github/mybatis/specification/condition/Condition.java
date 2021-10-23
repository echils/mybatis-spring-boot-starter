package com.github.mybatis.specification.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

import static com.github.mybatis.MybatisExpandContext.CONDITION_BETWEEN_VALUE_CONNECTOR;

/**
 * 动态查询参数
 *
 * @author echils
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Condition {

    /**
     * 参数
     */
    private String param;

    /**
     * 规则
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
        LIKE("LIKE"),
        NOT_LIKE("NOT LIKE"),
        LEFT_LIKE("LIKE"),
        RIGHT_LIKE("LIKE"),
        IN("IN"),
        NOT_IN("NOT IN"),
        IS_NULL("IS NULL"),
        IS_NOT_NULL("IS NOT NULL"),
        IS_BLANK("= ''"),
        IS_NOT_BLANK("!= ''"),
        //使用between时，value会是一个区间，可以用规定连接符{MybatisExpandContext.CONDITION_BETWEEN_VALUE_CONNECTOR}
        //连接两个值，例如 5&10。如果没有使用，那么默认前后区间为相同值.
        BETWEEN("BETWEEN"),
        //使用between时，value会是一个区间，可以用规定连接符{MybatisExpandContext.CONDITION_BETWEEN_VALUE_CONNECTOR}
        //连接两个值，例如 5&10。如果没有使用，那么默认前后区间为相同值.
        NOT_BETWEEN("NOT BETWEEN");

        public final String expression;

        Rule(String expression) {
            this.expression = expression;
        }
    }


    public void eq(String param, String value) {
        this.param = param;
        this.rule = Rule.EQ;
        this.value = value;
    }


    public void notEq(String param, String value) {
        this.param = param;
        this.rule = Rule.NOT_EQ;
        this.value = value;
    }

    public void lt(String param, String value) {
        this.param = param;
        this.rule = Rule.LT;
        this.value = value;
    }

    public void gt(String param, String value) {
        this.param = param;
        this.rule = Rule.GT;
        this.value = value;
    }

    public void le(String param, String value) {
        this.param = param;
        this.rule = Rule.LE;
        this.value = value;
    }

    public void ge(String param, String value) {
        this.param = param;
        this.rule = Rule.GE;
        this.value = value;
    }

    public void like(String param, String value) {
        this.param = param;
        this.rule = Rule.LIKE;
        this.value = value;
    }

    public void noLike(String param, String value) {
        this.param = param;
        this.rule = Rule.NOT_LIKE;
        this.value = value;
    }

    public void leftLike(String param, String value) {
        this.param = param;
        this.rule = Rule.LEFT_LIKE;
        this.value = value;
    }

    public void rightLike(String param, String value) {
        this.param = param;
        this.rule = Rule.RIGHT_LIKE;
        this.value = value;
    }

    public void in(String param, Collection collection) {
        this.param = param;
        this.rule = Rule.IN;
        this.value = collection;
    }

    public void in(String param, Object ...values) {
        this.param = param;
        this.rule = Rule.IN;
        this.value = values;
    }

    public void notIn(String param, Collection collection) {
        this.param = param;
        this.rule = Rule.NOT_IN;
        this.value = collection;
    }

    public void notIn(String param, Object ...values) {
        this.param = param;
        this.rule = Rule.NOT_IN;
        this.value = values;
    }

    public void isNull(String param) {
        this.param = param;
        this.rule = Rule.IS_NULL;
    }

    public void isNotNull(String param) {
        this.param = param;
        this.rule = Rule.IS_NOT_NULL;
    }

    public void isBlank(String param) {
        this.param = param;
        this.rule = Rule.IS_BLANK;
    }

    public void isNotBlank(String param) {
        this.param = param;
        this.rule = Rule.IS_NOT_BLANK;
    }

    public void between(String param, Object min, Object max) {
        this.param = param;
        this.rule = Rule.BETWEEN;
        this.value = min + CONDITION_BETWEEN_VALUE_CONNECTOR + max;
    }

    public void notBetween(String param, Object min, Object max) {
        this.param = param;
        this.rule = Rule.NOT_BETWEEN;
        this.value = min + CONDITION_BETWEEN_VALUE_CONNECTOR + max;
    }
    
}
