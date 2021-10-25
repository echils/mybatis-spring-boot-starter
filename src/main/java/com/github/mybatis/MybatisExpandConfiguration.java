package com.github.mybatis;

import com.github.mybatis.statement.MybatisStatementEnhancer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis增强配置类
 *
 * @author echils
 */
@Configuration
public class MybatisExpandConfiguration {


    @Bean
    public MybatisStatementEnhancer mybatisStatementEnhancer() {

        return new MybatisStatementEnhancer();
    }


    @Bean
    public MybatisExpandPostProcessor mybatisExpandPostProcessor(MybatisStatementEnhancer enhancer) {

        return new MybatisExpandPostProcessor(enhancer);
    }

}
