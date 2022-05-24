package com.github.mybatis.statement.metadata;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

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


    /**
     * 全局逻辑属性,当逻辑属性匹配时切换逻辑操作
     */
    private String logicalField;


    /**
     * 逻辑存在值
     */
    private String logicalExistValue = "0";


    /**
     * 逻辑删除值
     */
    private String logicalDeleteValue = "1";


    public boolean enableLogical(){
        return StringUtils.isNotBlank(logicalField);
    }

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
