package com.github.mybatis.statement;

import com.github.mybatis.MybatisExpandException;
import com.github.mybatis.annotations.Where;
import com.github.mybatis.specification.DynamicMapper;
import com.github.mybatis.specification.SpecificationMapper;
import com.github.mybatis.statement.loader.*;
import com.github.mybatis.statement.metadata.MappedMetaData;
import com.github.mybatis.statement.metadata.TableMetaData;
import com.github.mybatis.statement.resolver.TableMetaDataResolver;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.github.mybatis.MybatisExpandContext.ENTITY_RAW_INDEX;

/**
 * Mybatis原生Statement增强器
 *
 * @author echils
 */
@Slf4j
public class ExpandStatementEnhancer {


    @Autowired
    private TableMetaDataResolver tableMetaDataResolver;

    /**
     * 拓展功能加载器
     */
    private List<ExpandStatementLoader> expandStatementLoaders = new ArrayList<>();


    public ExpandStatementEnhancer() {
        initStatementLoader();
    }


    //加载内置拓展功能加载器
    private void initStatementLoader() {
        expandStatementLoaders.add(new DynamicCountStatementLoader());
        expandStatementLoaders.add(new DynamicFindAllStatementLoader());
        expandStatementLoaders.add(new DynamicMethodStatementLoader());
        expandStatementLoaders.add(new ExistByPrimaryKeyStatementLoader());
        expandStatementLoaders.add(new InsertBatchStatementLoader());
        expandStatementLoaders.add(new InsertSelectiveStatementLoader());
        expandStatementLoaders.add(new InsertStatementLoader());
        expandStatementLoaders.add(new SaveStatementLoader());
        expandStatementLoaders.add(new SaveAllStatementLoader());
        expandStatementLoaders.add(new SelectByPrimaryKeyStatementLoader());
        expandStatementLoaders.add(new UpdateBatchStatementLoader());
        expandStatementLoaders.add(new UpdateSelectiveBatchStatementLoader());
        expandStatementLoaders.add(new UpdateSelectiveStatementLoader());
        expandStatementLoaders.add(new UpdateStatementLoader());
        expandStatementLoaders.add(new DeleteByPrimaryKeyStatementLoader());
        expandStatementLoaders.add(new DeleteByPrimaryKeysStatementLoader());
    }


    /**
     * 对原生映射器进行增强，注册拓展功能
     */
    public void enhance(MapperFactoryBean<?> mapperFactoryBean) {
        Class<?> mapperInterface = mapperFactoryBean.getMapperInterface();
        Optional<Class<?>> entityOptional = parseEntityClazz(mapperInterface);
        if (!entityOptional.isPresent()) {
            throw new MybatisExpandException("The enhance mapper [{" +
                    mapperInterface + "}] corresponding entity class is invalid");
        }
        Class<?> entityClazz = entityOptional.get();
        TableMetaData tableMetaData = tableMetaDataResolver.resolve(entityClazz);
        Where whereAnnotation = entityClazz.getAnnotation(Where.class);
        String globalWhereClause = whereAnnotation == null ? null : whereAnnotation.clause();
        Arrays.stream(mapperInterface.getMethods()).forEach(method -> {
            MappedMetaData mappedMetaData = new MappedMetaData(entityClazz, mapperInterface,
                    method, tableMetaData, globalWhereClause, mapperFactoryBean);
            Optional<ExpandStatementLoader> statementLoaderOptional
                    = expandStatementLoaders.stream().filter(expandStatementLoader
                    -> expandStatementLoader.match(mappedMetaData)).findFirst();
            statementLoaderOptional.ifPresent(
                    expandStatementLoader -> expandStatementLoader.load(mappedMetaData));
        });
    }


    /**
     * 解析映射器绑定的实体类
     *
     * @param mapperInterface 映射器
     */
    private Optional<Class<?>> parseEntityClazz(Class<?> mapperInterface) {
        Type[] genericInterfaces = mapperInterface.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                Class<?> parameterizedRawClazz = (Class<?>) parameterizedType.getRawType();
                if (parameterizedRawClazz.isAssignableFrom(SpecificationMapper.class)
                        || parameterizedRawClazz.isAssignableFrom(DynamicMapper.class)) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    Class<?> actualTypeClazz = (Class<?>)
                            actualTypeArguments[actualTypeArguments.length - ENTITY_RAW_INDEX];
                    if (!actualTypeClazz.isAssignableFrom(Object.class)) {
                        return Optional.of(actualTypeClazz);
                    }
                }
            } else if (genericInterface instanceof Class) {
                Optional<Class<?>> clazzOptional =
                        parseEntityClazz((Class<?>) genericInterface);
                if (clazzOptional.isPresent()) { return clazzOptional; }
            }
        }
        return Optional.empty();
    }

}
