package com.github.mybatis.statement.metadata;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 表元数据信息
 *
 * @author echils
 */
@Data
public class TableMetaData {

    /**
     * 表名称
     */
    private String name;


    /**
     * 实体类名称
     */
    private String entityName;


    /**
     * 列信息(实体类和数据库表匹配的列)
     */
    private List<ColumnMetaData> columnMetaDataList = new ArrayList<>();


}
