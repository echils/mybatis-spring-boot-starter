package com.github.mybatis.statement.resolver;

import com.github.mybatis.MybatisExpandException;
import com.github.mybatis.statement.metadata.TableMetaData;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.github.mybatis.MybatisExpandContext.MariaDB;
import static com.github.mybatis.MybatisExpandContext.MySQL;

/**
 * 默认表元数据解析器
 *
 * @author echils
 */
@Slf4j
public class DefaultTableMetaDataResolver implements TableMetaDataResolver {


    @Autowired
    private TableNameResolver tableNameResolver;

    @Autowired
    private ColumnNameResolver columnNameResolver;

    /**
     * 会话
     */
    private SqlSession sqlSession;

    /**
     * 判断表是否存在的SQL
     */
    private static final String TABLE_INFO_SQL = "show tables like %s";

    /**
     * 查询表信息的SQL
     */
    private static final String COLUMN_INFO_SQL = "SELECT column_name as columnName, data_type as dataType, " +
            "column_key = 'pri' as primaryKey FROM information_schema.COLUMNS WHERE " +
            "table_name = %s AND table_schema = (SELECT DATABASE())";


    @Override
    public TableMetaData resolve(Class<?> entityClazz) {
        String tableName = tableNameResolver.resolveTableName(entityClazz);


        return null;
    }

    /**
     * 判断是否数据库类型
     */
    private boolean support() {
        try (Connection connection = sqlSession.getConfiguration()
                .getEnvironment().getDataSource().getConnection()) {
            if (connection.isClosed()) {
                log.warn("Database connection is closed");
                return false;
            }
            String databaseType = connection.getMetaData().getDatabaseProductName();
            return MySQL.equalsIgnoreCase(databaseType) || MariaDB.equalsIgnoreCase(databaseType);
        } catch (SQLException e) {
            throw new MybatisExpandException(e);
        }
    }


    /**
     * 判断数据库中是否存在这张表
     *
     * @param tableName 表名称
     */
    private boolean existTable(String tableName) {
        try {
            String sql = String.format(TABLE_INFO_SQL, tableName);
            ResultSet resultSet = sqlSession.getConnection().createStatement().executeQuery(sql);
            return resultSet != null;
        } catch (SQLException e) {
            throw new MybatisExpandException(e);
        }
    }


}
