package com.github.mybatis.statement.resolver;

import com.github.mybatis.annotations.Table;
import org.apache.commons.lang3.StringUtils;

import static com.github.mybatis.MybatisExpandContext.COLUMN_ESCAPE_PARAM;
import static com.github.mybatis.MybatisExpandContext.humpToUnderlineFunction;

/**
 * 默认的表名称解析器
 *
 * @author echils
 */
public class DefaultTableNameResolver implements TableNameResolver {

    @Override
    public String resolveTableName(Class<?> tableEntityClazz) {
        Table tableAnnotation = tableEntityClazz.getAnnotation(Table.class);
        if (tableAnnotation != null) {
            String tableName = tableAnnotation.value();
            if (StringUtils.isNotBlank(tableName)) {
                if (tableName.startsWith(String.valueOf(COLUMN_ESCAPE_PARAM)) &&
                        tableName.endsWith(String.valueOf(COLUMN_ESCAPE_PARAM))) {
                    tableName = tableName.substring(tableName.indexOf(COLUMN_ESCAPE_PARAM) + 1,
                            tableName.lastIndexOf(COLUMN_ESCAPE_PARAM));
                }
                return tableName;
            }
        }
        return humpToUnderlineFunction.apply(tableEntityClazz.getSimpleName());
    }

}
