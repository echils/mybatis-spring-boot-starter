package com.github.mybatis;

import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

/**
 * 上下文定义通用变量和函数
 *
 * @author echils
 */
public class MybatisExpandContext {

    /**
     * MYSQL关键字转义符
     */
    public static final char COLUMN_ESCAPE_PARAM = '`';

    /**
     * MYSQL模糊查询关键字
     */
    public static final char COLUMN_LIKE_PARAM = '%';

    /**
     * MYSQL多参数连接符
     */
    public static final String CONDITION_BETWEEN_VALUE_CONNECTOR = "&";


    /**
     * MYSQL关键字转义函数
     */
    public static final Function<String, String> COLUMN_ESCAPE_FUNCTION = column -> {
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


}
