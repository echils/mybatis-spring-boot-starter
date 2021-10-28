package com.github.mybatis.statement;

import com.github.mybatis.statement.loader.*;
import com.github.mybatis.statement.resolver.TableMetaDataResolver;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

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
     * 对原声MapperFactory进行增强，注册拓展功能
     */
    public void enhance(MapperFactoryBean<?> mapperFactoryBean) {

        Class<?> mapperInterface = mapperFactoryBean.getMapperInterface();


    }

    /**
     * 父子接口鉴定
     *
     * @param clazz       检测接口
     * @param parentClazz 父接口
     */
    private boolean paternityInterfaceTest(Class<?> clazz, Class<?> parentClazz) {
        if (clazz == null || parentClazz == null) {
            return false;
        }
        return false;
    }
}
