package com.github.mybatis.statement.loader;

import com.github.mybatis.statement.metadata.MappedMetaData;
import org.apache.ibatis.mapping.MappedStatement;

import java.util.Optional;

/**
 * 为拓展功能进行加载管理
 *
 * @author echils
 */
public interface ExpandStatementLoader {

    /**
     * 加载拓展功能
     *
     * @param mappedMetaData 拓展功能映射元数据
     */
    Optional<MappedStatement> load(MappedMetaData mappedMetaData);


    /**
     * 拓展方法是否匹配
     *
     * @param mappedMetaData 拓展功能映射元数据
     */
    boolean match(MappedMetaData mappedMetaData);

}
