package com.github.mybatis;

import com.github.mybatis.statement.ExpandStatementEnhancer;
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

    private ExpandStatementEnhancer expandStatementEnhancer;

    @Autowired
    public MybatisExpandPostProcessor(ExpandStatementEnhancer expandStatementEnhancer) {
        this.expandStatementEnhancer = expandStatementEnhancer;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof MapperFactoryBean) {
            MapperFactoryBean<?> mapperFactoryBean = (MapperFactoryBean<?>) bean;
            expandStatementEnhancer.enhance(mapperFactoryBean);
            log.debug("Enhance mapper [{}] complete ", mapperFactoryBean.getMapperInterface().getName());
        }
        return bean;
    }

}
