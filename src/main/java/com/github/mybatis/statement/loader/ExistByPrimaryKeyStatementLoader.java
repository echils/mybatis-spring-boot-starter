package com.github.mybatis.statement.loader;

import com.github.mybatis.statement.metadata.MappedMetaData;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 判断主键对应的数据是否存在功能加载器
 *
 * @author echils
 */
@Slf4j
public class ExistByPrimaryKeyStatementLoader extends AbstractExpandStatementLoader {

    /**
     * 拓展方法名
     */
    private static final String EXPAND_STATEMENT_METHOD = "existByPrimaryKey";


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
        return EXPAND_STATEMENT_METHOD.equals(mappedMetaData.getMappedMethod().getName());
    }

}
