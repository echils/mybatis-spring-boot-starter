package com.github.mybatis.specification.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import java.util.Collection;


/**
 * 动态查询参数
 *
 * @author echils
 */
@Getter
@ToString
@NoArgsConstructor
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
        BETWEEN("BETWEEN"),
        NOT_BETWEEN("NOT BETWEEN");

        public final String value;

        Rule(String value) {
            this.value = value;
        }
    }


    public Condition(@NonNull String param, @NonNull Rule rule) {
        this.param = param;
        this.rule = rule;
    }

    public Condition(@NonNull String param, @NonNull Rule rule, @NonNull Object value) {
        this.param = param;
        this.rule = rule;
        this.value = value;
    }

    public void eq(@NonNull String param, @NonNull Object value) {
        this.param = param;
        this.rule = Rule.EQ;
        this.value = value;
    }

    public void notEq(@NonNull String param, @NonNull Object value) {
        this.param = param;
        this.rule = Rule.NOT_EQ;
        this.value = value;
    }

    public void lt(@NonNull String param, @NonNull Object value) {
        this.param = param;
        this.rule = Rule.LT;
        this.value = value;
    }

    public void gt(@NonNull String param, @NonNull Object value) {
        this.param = param;
        this.rule = Rule.GT;
        this.value = value;
    }

    public void le(@NonNull String param, @NonNull Object value) {
        this.param = param;
        this.rule = Rule.LE;
        this.value = value;
    }

    public void ge(@NonNull String param, @NonNull Object value) {
        this.param = param;
        this.rule = Rule.GE;
        this.value = value;
    }

    public void like(@NonNull String param, @NonNull Object value) {
        this.param = param;
        this.rule = Rule.LIKE;
        this.value = "%" + value + "%";
    }

    public void noLike(@NonNull String param, @NonNull Object value) {
        this.param = param;
        this.rule = Rule.NOT_LIKE;
        this.value = "%" + value + "%";
    }

    public void leftLike(@NonNull String param, @NonNull Object value) {
        this.param = param;
        this.rule = Rule.LEFT_LIKE;
        this.value = "%" + value;
    }

    public void rightLike(@NonNull String param, @NonNull Object value) {
        this.param = param;
        this.rule = Rule.RIGHT_LIKE;
        this.value = value + "%";
    }

    public void in(@NonNull String param, @NonNull Collection<?> collection) {
        this.param = param;
        this.rule = Rule.IN;
        this.value = collection;
    }

    public void in(@NonNull String param, @NonNull Object... values) {
        this.param = param;
        this.rule = Rule.IN;
        this.value = values;
    }

    public void notIn(@NonNull String param, @NonNull Collection<?> collection) {
        this.param = param;
        this.rule = Rule.NOT_IN;
        this.value = collection;
    }

    public void notIn(@NonNull String param, @NonNull Object... values) {
        this.param = param;
        this.rule = Rule.NOT_IN;
        this.value = values;
    }

    public void isNull(@NonNull String param) {
        this.param = param;
        this.rule = Rule.IS_NULL;
    }

    public void isNotNull(@NonNull String param) {
        this.param = param;
        this.rule = Rule.IS_NOT_NULL;
    }

    public void isBlank(@NonNull String param) {
        this.param = param;
        this.rule = Rule.IS_BLANK;
    }

    public void isNotBlank(@NonNull String param) {
        this.param = param;
        this.rule = Rule.IS_NOT_BLANK;
    }

    public void between(@NonNull String param, @NonNull Object min, @NonNull Object max) {
        this.param = param;
        this.rule = Rule.BETWEEN;
        this.value = min + " AND " + max;
    }

    public void notBetween(@NonNull String param, @NonNull Object min, @NonNull Object max) {
        this.param = param;
        this.rule = Rule.NOT_BETWEEN;
        this.value = min + " AND " + max;
    }

}
