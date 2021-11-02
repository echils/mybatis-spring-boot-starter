package com.github.mybatis.statement.resolver;

import com.github.mybatis.annotations.Column;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

import static com.github.mybatis.MybatisExpandContext.humpToUnderlineFunction;

/**
 * 默认的列名称解析器
 *
 * @author echils
 */
public class DefaultColumnNameResolver implements ColumnNameResolver {

    @Override
    public String resolveTableName(Field field) {
        Column tableAnnotation = field.getAnnotation(Column.class);
        if (tableAnnotation != null) {
            String tableName = tableAnnotation.value();
            if (StringUtils.isNotBlank(tableName)) { return tableName; }
        }
        return humpToUnderlineFunction.apply(field.getName());
    }

}
