package com.github.mybatis.statement;

import com.github.mybatis.MybatisExpandException;
import com.github.mybatis.annotations.Where;
import com.github.mybatis.specification.DynamicMapper;
import com.github.mybatis.specification.SpecificationMapper;
import com.github.mybatis.statement.loader.*;
import com.github.mybatis.statement.metadata.ColumnMetaData;
import com.github.mybatis.statement.metadata.MappedMetaData;
import com.github.mybatis.statement.metadata.TableMetaData;
import com.github.mybatis.statement.resolver.TableMetaDataResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
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
//        expandStatementLoaders.add(new DynamicCountStatementLoader());
//        expandStatementLoaders.add(new DynamicFindAllStatementLoader());
        expandStatementLoaders.add(new ExistByPrimaryKeyStatementLoader());
        expandStatementLoaders.add(new InsertBatchStatementLoader());
        expandStatementLoaders.add(new InsertSelectiveStatementLoader());
        expandStatementLoaders.add(new InsertStatementLoader());
        expandStatementLoaders.add(new DeleteByPrimaryKeysStatementLoader());
        expandStatementLoaders.add(new DeleteByPrimaryKeyStatementLoader());
        expandStatementLoaders.add(new UpdateBatchStatementLoader());
        expandStatementLoaders.add(new UpdateSelectiveBatchStatementLoader());
        expandStatementLoaders.add(new UpdateSelectiveStatementLoader());
        expandStatementLoaders.add(new UpdateStatementLoader());
        expandStatementLoaders.add(new SelectByPrimaryKeysStatementLoader());
        expandStatementLoaders.add(new SelectByPrimaryKeyStatementLoader());
        expandStatementLoaders.add(new DynamicMethodStatementLoader());
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
        TableMetaData tableMetaData =
                tableMetaDataResolver.resolve(mapperFactoryBean.getSqlSession(), entityClazz);
        Optional<ColumnMetaData> primaryKeyColumnOptional = tableMetaData.getColumnMetaDataList()
                .stream().filter(ColumnMetaData::isPrimaryKey).findFirst();
        Where whereAnnotation = entityClazz.getAnnotation(Where.class);
        String globalWhereClause = whereAnnotation == null ? null : whereAnnotation.clause();

        if (isNecessaryOfPrimaryKey(mapperInterface) && !primaryKeyColumnOptional.isPresent()) {
            throw new MybatisExpandException("The entity class [" + entityClazz + "] has no primary key in the " +
                    "corresponding table,please add primary key for it or use " +
                    "the expandMapper [" + DynamicMapper.class.getName() + "]");
        }

        Arrays.stream(mapperInterface.getMethods())
                .filter(method -> !method.isDefault() && !method.isBridge()).forEach(method -> {
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
                    Type actualTypeArgument = actualTypeArguments[actualTypeArguments.length - ENTITY_RAW_INDEX];
                    if (actualTypeArgument instanceof TypeVariable) {
                        return Optional.empty();
                    }
                    if (actualTypeArgument instanceof Class) {
                        Class<?> actualTypeClazz = (Class<?>) actualTypeArgument;
                        if (!actualTypeClazz.isAssignableFrom(Object.class)) {
                            return Optional.of(actualTypeClazz);
                        }
                    }
                }
            } else if (genericInterface instanceof Class) {
                Optional<Class<?>> clazzOptional = parseEntityClazz((Class<?>) genericInterface);
                if (clazzOptional.isPresent()) { return clazzOptional; }
            }
        }
        return Optional.empty();
    }


    /**
     * 判断该映射器是否需要主键
     *
     * @param clazz 需要拓展的业务映射器
     */
    private boolean isNecessaryOfPrimaryKey(Class<?> clazz) {
        Class<?>[] interfaces = clazz.getInterfaces();
        if (ArrayUtils.isNotEmpty(interfaces)) {
            for (Class<?> interfaceBody : interfaces) {
                return interfaceBody.getName().equals(SpecificationMapper.class.getName())
                        || isNecessaryOfPrimaryKey(interfaceBody);
            }
        }
        return false;
    }

}
