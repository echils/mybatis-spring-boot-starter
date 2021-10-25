package com.github.mybatis;

import com.github.mybatis.statement.MybatisStatementEnhancer;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Mybatis拓展后置处理器
 *
 * @author echils
 */
@Slf4j
public class MybatisExpandPostProcessor implements BeanPostProcessor {

    private MybatisStatementEnhancer mybatisStatementEnhancer;

    @Autowired
    public MybatisExpandPostProcessor(MybatisStatementEnhancer mybatisStatementEnhancer) {
        this.mybatisStatementEnhancer = mybatisStatementEnhancer;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof MapperFactoryBean) {
            MapperFactoryBean<?> mapperFactoryBean = (MapperFactoryBean<?>) bean;
            mybatisStatementEnhancer.enhance(mapperFactoryBean);
            log.debug("Enhance mapper [{}] complete ", mapperFactoryBean.getMapperInterface().getName());
        }
        return bean;
    }

}
