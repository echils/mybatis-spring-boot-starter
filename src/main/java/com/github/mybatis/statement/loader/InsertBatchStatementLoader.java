package com.github.mybatis.statement.loader;

import com.github.mybatis.statement.metadata.MappedMetaData;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.MappedStatement;

import java.util.Optional;

/**
 * 批量插入功能加载器
 *
 * @author echils
 */
@Slf4j
public class InsertBatchStatementLoader implements ExpandStatementLoader {


    @Override
    public Optional<MappedStatement> load(MappedMetaData mappedMetaData) {
        return Optional.empty();
    }

    @Override
    public boolean match(MappedMetaData mappedMetaData) {
        return false;
    }

}
