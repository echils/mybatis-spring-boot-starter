package com.github.mybatis.specification.condition;

import lombok.Data;

/**
 * Limit参数
 *
 * @author echils
 */
@Data
public class Limit {

    /**
     * 起始下标
     */
    private int index;

    /**
     * 长度
     */
    private int size;


    public Limit(int size) {
        if (size > 0) {
            this.size = size;
        }
    }

    public Limit(int index, int size) {
        if (index > 0) {
            this.index = index;
        }
        if (size > 0) {
            this.size = size;
        }
    }
}
