package com.github.mybatis.statement.loader;

import com.github.mybatis.specification.SpecificationMapper;
import com.github.mybatis.statement.metadata.MappedMetaData;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 选择更新功能加载器
 *
 * @author echils
 */
@Slf4j
public class UpdateSelectiveStatementLoader extends AbstractExpandStatementLoader {

    /**
     * 拓展方法名
     */
    private static final String EXPAND_STATEMENT_METHOD
            = SpecificationMapper.class.getName() + ".updateSelective";

    @Override
    SqlCommandType sqlCommandType() {
        return null;
    }

    @Override
    SqlSource sqlSourceBuild(MappedMetaData mappedMetaData) {
        return null;
    }

    @Override
    public boolean match(MappedMetaData mappedMetaData) {
        return mappedMetaData.getMappedMethod().toString().contains(EXPAND_STATEMENT_METHOD);
    }
}
