package com.github.mybatis.statement.loader;

import com.github.mybatis.statement.metadata.MappedMetaData;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.session.Configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * 抽象拓展功能加载器
 *
 * @author echils
 */
@Slf4j
public abstract class AbstractExpandStatementLoader implements ExpandStatementLoader {


    @Override
    public final Optional<MappedStatement> load(MappedMetaData mappedMetaData) {

        Configuration configuration = mappedMetaData
                .getMapperFactoryBean().getSqlSession().getConfiguration();
        Collection<String> mappedStatementNames
                = configuration.getMappedStatementNames();
        String mappedStatementId = mappedMetaData.getMappedStatementId();

        if (mappedStatementNames.contains(mappedStatementId)) {
            log.info("The statement [{}] has custom definition," +
                    "cancel the expand statement load", mappedStatementId);
            return Optional.ofNullable(configuration.getMappedStatement(mappedStatementId));
        }

        SqlCommandType sqlCommandType = sqlCommandType();
        boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
        String namespace = mappedMetaData.getMapperInterface().getName();
        boolean hasCache = configuration.hasCache(namespace);
        MappedStatement mappedStatement = new MappedStatement.Builder(configuration,
                mappedStatementId, sqlSourceBuild(mappedMetaData), sqlCommandType)
                .resource(namespace)
                .statementType(StatementType.PREPARED)
                .databaseId(configuration.getDatabaseId())
                .resultSetType(configuration.getDefaultResultSetType())
                .flushCacheRequired(hasCache && !isSelect)
                .useCache(hasCache && isSelect)
                .resultMaps(Collections.singletonList(mappedMetaData.getDefaultResultMap()))
                .cache(hasCache ? configuration.getCache(namespace) : null).build();

        Optional<MappedStatement> statementOptional = Optional.ofNullable(mappedStatement);
        statementOptional.ifPresent(configuration::addMappedStatement);

        return statementOptional;

    }


    /**
     * 获取SQL命令类型
     */
    abstract SqlCommandType sqlCommandType();


    /**
     * 获取SQL语句源
     *
     * @param mappedMetaData 元数据
     */
    abstract SqlSource sqlSourceBuild(MappedMetaData mappedMetaData);


}
