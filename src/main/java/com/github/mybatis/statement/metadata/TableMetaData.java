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
public class TableMetaData implements Cloneable {

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


    @Override
    protected TableMetaData clone() throws CloneNotSupportedException {
        TableMetaData cloneData = (TableMetaData) super.clone();
        List<ColumnMetaData> cloneColumnMetaDataList = new ArrayList<>();
        for (ColumnMetaData columnMetaData : columnMetaDataList) {
            cloneColumnMetaDataList.add(columnMetaData.clone());
        }
        cloneData.setColumnMetaDataList(cloneColumnMetaDataList);
        return cloneData;
    }

}
