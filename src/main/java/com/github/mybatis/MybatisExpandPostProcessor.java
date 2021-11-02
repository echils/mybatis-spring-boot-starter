package com.github.mybatis;

import com.github.mybatis.specification.DynamicMapper;
import com.github.mybatis.specification.SpecificationMapper;
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
            if (needEnhance(mapperFactoryBean.getMapperInterface())) {
                expandStatementEnhancer.enhance(mapperFactoryBean);
                log.debug("Enhance mapper [{}] complete ", mapperFactoryBean.getMapperInterface().getName());
            }
        }
        return bean;
    }

    /**
     * 验证是否需要增强
     *
     * @param mapperInterface 代验证的映射器
     */
    private boolean needEnhance(Class<?> mapperInterface) {
        Class<?>[] interfaces = mapperInterface.getInterfaces();
        for (Class<?> body : interfaces) {
            if (body.isAssignableFrom(SpecificationMapper.class) ||
                    body.isAssignableFrom(DynamicMapper.class) || needEnhance(body)) {
                return true;
            }
        }
        return false;
    }

}
