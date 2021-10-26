package com.github.mybatis;

import com.github.mybatis.statement.ExpandStatementEnhancer;
import com.github.mybatis.statement.resolver.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis增强配置类
 *
 * @author echils
 */
@Configuration
public class MybatisExpandConfiguration {


    /**
     * 数据库列名解析器
     */
    @Bean
    @ConditionalOnMissingBean(ColumnNameResolver.class)
    public ColumnNameResolver columnNameResolver() {
        return new DefaultColumnNameResolver();
    }


    /**
     * 数据库表名解析器
     */
    @Bean
    @ConditionalOnMissingBean(TableNameResolver.class)
    public TableNameResolver tableNameResolver() {
        return new DefaultTableNameResolver();
    }


    /**
     * 数据库表解析器
     */
    @Bean
    @ConditionalOnMissingBean(TableMetaDataResolver.class)
    public TableMetaDataResolver tableMetaDataResolver() {
        return new DefaultTableMetaDataResolver();
    }


    /**
     * Mybatis拓展增强器
     */
    @Bean
    public ExpandStatementEnhancer mybatisStatementEnhancer() {
        return new ExpandStatementEnhancer();
    }


    /**
     * Mybatis拓展后置处理器
     *
     * @param enhancer Mybatis拓展增强器
     */
    @Bean
    public MybatisExpandPostProcessor mybatisExpandPostProcessor(
            ExpandStatementEnhancer enhancer) {
        return new MybatisExpandPostProcessor(enhancer);
    }


}
