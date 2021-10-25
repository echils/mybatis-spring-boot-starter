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
     * 列信息
     */
    private List<ColumnMetaData> columnMetaDataList = new ArrayList<>();


}
