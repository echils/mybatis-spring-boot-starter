package com.github.mybatis;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
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
     * 是否开启全局逻辑支持
     */
    public boolean enableGlobalLogical() {
        return StringUtils.isNotBlank(globalLogical.logicalField);
    }


    /**
     * 全局逻辑列信息
     */
    @Data
    public static class GlobalLogical {

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

    }




}
