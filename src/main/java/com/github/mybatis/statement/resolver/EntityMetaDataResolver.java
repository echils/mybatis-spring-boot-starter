package com.github.mybatis.statement.resolver;

import com.github.mybatis.statement.metadata.EntityMetaData;
import org.apache.ibatis.session.SqlSession;

/**
 * 解析实体类信息
 *
 * @author echils
 */
public interface EntityMetaDataResolver {


    /**
     * 解析实体类
     *
     * @param sqlSession  会话
     * @param entityClass 实体类
     */
    EntityMetaData resolve(SqlSession sqlSession, Class<?> entityClass);


}
