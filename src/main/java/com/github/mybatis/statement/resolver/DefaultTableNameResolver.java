package com.github.mybatis.statement.resolver;

import com.github.mybatis.annotations.Table;
import org.apache.commons.lang3.StringUtils;

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
            if (StringUtils.isNotBlank(tableName)) { return tableName; }
        }
        return humpToUnderlineFunction.apply(tableEntityClazz.getSimpleName());
    }

}
