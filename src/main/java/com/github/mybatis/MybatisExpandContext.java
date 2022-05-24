package com.github.mybatis;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 上下文定义通用变量和函数
 *
 * @author echils
 */
public class MybatisExpandContext {


    /**
     * 数据库类型-MySQL
     */
    public static final String EXPAND_PROPERTIES_PREFIX = "mybatis.enhance";

    /**
     * MYSQL关键字转义符
     */
    public static final char COLUMN_ESCAPE_PARAM = '`';

    /**
     * 数据库类型-MySQL
     */
    public static final String MySQL = "MySQL";

    /**
     * 数据库类型-MariaDB
     */
    public static final String MariaDB = "MariaDB";

    /**
     * 泛型Entity默认索引
     */
    public static final Integer ENTITY_RAW_INDEX = 1;

    /**
     * 唯一主键索引
     */
    public static final Integer UNIQUE_PRIMARY_KEY_INDEX = 1;

    /**
     * 未定义标签
     */
    public static final String UNDEFINED_LABEL = "UNDEFINED";

    /**
     * 内置结果集名称后缀
     */
    public static final String EXPAND_DEFAULT_RESULT_MAP = "ExpandResultMap";

    /**
     * Mybatis XML 参数表达式
     */
    public static final String MYBATIS_FOREACH_PARAM = "item";

    /**
     * Mybatis XML 参数表达式
     */
    public static final String MYBATIS_PARAM_EXPRESSION = "#{%s,jdbcType=%s}";

    /**
     * Mybatis XML 参数表达式
     */
    public static final String MYBATIS_PARAM_SIMPLE_EXPRESSION = "#{%s}";

    /**
     * Mybatis XML 参数表达式
     */
    public static final String MYBATIS_TEST_EXPRESSION = "%s != null";

    /**
     * Mybatis XML 参数表达式
     */
    public static final String MYBATIS_COLLECTION_EXPRESSION = "%s != null && %s.size >0 ";

    /**
     * MySQL 条件表达式
     */
    public static final String MYBATIS_WHERE_EXPRESSION = "WHERE";

    /**
     * MYSQL关键字转义函数
     */
    public static final Function<String, String> KEYWORDS_ESCAPE_FUNCTION = column -> {
        if (StringUtils.isNotBlank(column)) {
            if (column.charAt(0) != COLUMN_ESCAPE_PARAM) {
                column = COLUMN_ESCAPE_PARAM + column;
            }
            if (column.charAt(column.length() - 1) != COLUMN_ESCAPE_PARAM) {
                column = column + COLUMN_ESCAPE_PARAM;
            }
        }
        return column;
    };

    /**
     * 驼峰转下划线函数
     *
     * 示例：HelloWorld-> hello_world
     */
    public static final Function<String, String> humpToUnderlineFunction = source -> {
        if (StringUtils.isBlank(source)) return source;
        StringBuilder builder = new StringBuilder();
        char[] chars = source.toCharArray();
        char firstChar = chars[0];
        builder.append(firstChar >= 'A' && firstChar <= 'Z' ? Character.toLowerCase(firstChar) : firstChar);
        for (int i = 1; i < chars.length; i++) {
            char c = chars[i];
            builder.append(c >= 'A' && c <= 'Z' ? "_" + Character.toLowerCase(c) : c);
        }
        return builder.toString();
    };

    /**
     * 下划线转驼峰函数（如果first为false，跳过首字母）
     *
     * 示例： hello_world -> HelloWorld
     */
    public static final BiFunction<String, Boolean, String> underlineToHumpFunction = (source, first) -> {
        StringBuilder builder = new StringBuilder();
        char[] chars = source.toCharArray();
        boolean upper = first;
        for (char c : chars) {
            if (c == '_') {
                upper = true;
            } else {
                builder.append(upper ? Character.toUpperCase(c) : c);
                upper = false;
            }
        }
        return builder.toString();
    };


    /**
     * 递归获取实体类所有属性
     *
     * @param entityClazz 实体类
     */
    public static List<Field> obtainEntityFields(Class<?> entityClazz) {
        if (entityClazz == null || entityClazz.isInterface()) {
            return Collections.emptyList();
        }
        List<Field> fields = new ArrayList<>();
        fields.addAll(obtainEntityFields(entityClazz.getSuperclass()));
        fields.addAll(Arrays.asList(entityClazz.getDeclaredFields()));
        return fields;
    }

}
