package com.github.mybatis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.github.mybatis.MybatisExpandContext.EXPAND_PROPERTIES_PREFIX;

/**
 * Mybatis增强参数定义
 *
 * @author echils
 */
@Data
@ConfigurationProperties(EXPAND_PROPERTIES_PREFIX)
public class MybatisExpandProperties {


    /**
     * 全局逻辑列配置
     */
    private GlobalLogical globalLogical = new GlobalLogical();


    /**
     * 全局逻辑列信息
     */
    @Data
    public static class GlobalLogical implements Cloneable {

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


        @Override
        protected GlobalLogical clone() throws CloneNotSupportedException {
            return (GlobalLogical) super.clone();
        }

    }




}
