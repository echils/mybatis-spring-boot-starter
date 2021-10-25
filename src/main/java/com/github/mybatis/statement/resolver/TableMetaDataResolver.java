package com.github.mybatis.statement.resolver;

import com.github.mybatis.statement.metadata.TableMetaData;

/**
 * 解析实体类对应的表信息
 *
 * @author echils
 */
public interface TableMetaDataResolver {


    /**
     * 解析实体类对应的表
     *
     * @param tableName 表名称
     */
    TableMetaData resolve(String tableName);


}
