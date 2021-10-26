package com.github.mybatis.statement.loader;

import com.github.mybatis.statement.metadata.MappedMetaData;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.MappedStatement;

import java.util.Optional;

/**
 * 批量更新功能加载器
 *
 * @author echils
 */
@Slf4j
public class UpdateBatchStatementLoader implements ExpandStatementLoader {


    @Override
    public Optional<MappedStatement> load(MappedMetaData mappedMetaData) {
        return Optional.empty();
    }

    @Override
    public boolean match(MappedMetaData mappedMetaData) {
        return false;
    }

}
