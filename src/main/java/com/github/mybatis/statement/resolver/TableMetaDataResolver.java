package com.github.mybatis.statement.resolver;

import com.github.mybatis.statement.metadata.TableMetaData;
import org.apache.ibatis.session.SqlSession;

/**
 * 解析实体类对应的表信息
 *
 * @author echils
 */
public interface TableMetaDataResolver {

    /**
     * 解析实体类对应的表
     *
     * @param sqlSession  会话
     * @param entityClazz 实体类
     */
    TableMetaData resolve(SqlSession sqlSession, Class<?> entityClazz);

}
