package com.github.mybatis.statement.resolver;

import com.github.mybatis.MybatisExpandException;
import com.github.mybatis.annotations.Column;
import com.github.mybatis.annotations.Table;
import com.github.mybatis.statement.metadata.ColumnMetaData;
import com.github.mybatis.statement.metadata.MysqlTypeMetaData;
import com.github.mybatis.statement.metadata.TableMetaData;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.github.mybatis.MybatisExpandContext.*;

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
     * 数据库类型支持性的校验只需进行一次即可
     */
    private AtomicBoolean alreadyCheck = new AtomicBoolean(false);

    /**
     * 判断表是否存在的SQL
     */
    private static final String TABLE_INFO_SQL = "show tables like '%s'";

    /**
     * 查询表信息的SQL
     */
    private static final String COLUMN_INFO_SQL = "SELECT column_name as columnName," +
            " data_type as dataType, column_key = 'pri' as primaryKey FROM" +
            " information_schema.COLUMNS WHERE table_name = '%s' AND table_schema = (SELECT DATABASE())";


    @Override
    public TableMetaData resolve(SqlSession sqlSession, Class<?> entityClazz) {

        checkDatabaseTypeSupported(sqlSession);
        String tableName = tableNameResolver.resolveTableName(entityClazz);
        if (!existTable(sqlSession, tableName)) {
            throw new MybatisExpandException("The table [" + tableName + "] does not exist in the database");
        }

        TableMetaData tableMetaData = new TableMetaData();
        tableMetaData.setName(tableName);
        tableMetaData.setEntityName(entityClazz.getSimpleName());
        Map<String, DatabaseColumnInfo> databaseColumnInfoMap
                = obtainDatabaseColumnInfo(sqlSession, tableName).stream()
                .collect(Collectors.toMap(DatabaseColumnInfo::getColumnName,
                        databaseColumnInfo -> databaseColumnInfo));

        tableMetaData.setColumnMetaDataList(obtainEntityFields(entityClazz).stream()
                .map(field -> {
                    ColumnMetaData columnMetaData = new ColumnMetaData();
                    columnMetaData.setColumnName(columnNameResolver.resolveTableName(field));
                    columnMetaData.setJavaType(field.getType());
                    columnMetaData.setFieldName(field.getName());
                    Column columnAnnotation = field.getAnnotation(Column.class);
                    if (columnAnnotation != null) {
                        columnMetaData.setDefaultInsertValue(columnAnnotation.defaultInsertValue());
                        columnMetaData.setDefaultInsertValue(columnAnnotation.defaultUpdateValue());
                        columnMetaData.setUpdatable(columnAnnotation.updatable());
                    }
                    return columnMetaData;
                })
                .filter(columnMetaData -> databaseColumnInfoMap.containsKey(columnMetaData.getColumnName()))
                .peek(columnMetaData -> {
                    DatabaseColumnInfo databaseColumnInfo = databaseColumnInfoMap.get(columnMetaData.getColumnName());
                    columnMetaData.setPrimaryKey(databaseColumnInfo.isPrimaryKey());
                    columnMetaData.setJdbcType(databaseColumnInfo.getJdbcType());
                }).collect(Collectors.toList()));

        Table tableAnnotation = entityClazz.getAnnotation(Table.class);
        if (tableAnnotation != null) {
            String logicalField = tableAnnotation.logicalField();
            if (tableMetaData.getColumnMetaDataList().stream().noneMatch(
                    columnMetaData -> columnMetaData.getFieldName().equals(logicalField))) {
                throw new MybatisExpandException("The entity [" +
                        tableMetaData.getEntityName() + "] does not exist the logical field [" + logicalField + "]");
            }
            tableMetaData.setLogicalField(logicalField);
            tableMetaData.setLogicalExistValue(tableAnnotation.existValue());
            tableMetaData.setLogicalDeleteValue(tableAnnotation.deleteValue());
        }

        return tableMetaData;
    }

    /**
     * 判断数据库类型是否支持
     */
    private void checkDatabaseTypeSupported(SqlSession sqlSession) {
        if (!alreadyCheck.get()) {
            try (Connection connection = sqlSession.getConfiguration()
                    .getEnvironment().getDataSource().getConnection()) {
                if (!connection.isClosed()) {
                    String databaseType = connection.getMetaData().getDatabaseProductName();
                    if (!(MySQL.equalsIgnoreCase(databaseType) || MariaDB.equalsIgnoreCase(databaseType))) {
                        throw new MybatisExpandException("This database type [" + databaseType + "] is not currently supported");
                    }
                    alreadyCheck.compareAndSet(false, true);
                }
            } catch (SQLException e) {
                throw new MybatisExpandException(e);
            }
        }
    }

    /**
     * 判断数据库中是否存在这张表
     *
     * @param sqlSession 会话
     * @param tableName  表名称
     */
    private boolean existTable(SqlSession sqlSession, String tableName) {
        String sql = String.format(TABLE_INFO_SQL, tableName);
        try (Connection connection = sqlSession
                .getConfiguration().getEnvironment().getDataSource().getConnection()) {
            try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
                return resultSet != null && resultSet.next();
            }
        } catch (SQLException e) {
            throw new MybatisExpandException(e);
        }
    }

    /**
     * 获取数据库表信息
     *
     * @param sqlSession 会话
     * @param tableName  表名称
     */
    private List<DatabaseColumnInfo> obtainDatabaseColumnInfo(SqlSession sqlSession, String tableName) {
        List<DatabaseColumnInfo> databaseColumnInfoList = new ArrayList<>();
        String sql = String.format(COLUMN_INFO_SQL, tableName);
        try (Connection connection = sqlSession.getConfiguration().getEnvironment().getDataSource().getConnection()) {
            try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
                while (resultSet != null && resultSet.next()) {
                    DatabaseColumnInfo databaseColumnInfo = new DatabaseColumnInfo();
                    databaseColumnInfo.setColumnName(resultSet.getString(1));
                    databaseColumnInfo.setJdbcType(MysqlTypeMetaData.nameOf(resultSet.getString(2)).getJdbcType());
                    databaseColumnInfo.setPrimaryKey(resultSet.getBoolean(3));
                    databaseColumnInfoList.add(databaseColumnInfo);
                }
            }
        } catch (SQLException e) {
            throw new MybatisExpandException(e);
        }
        return databaseColumnInfoList;
    }

    /**
     * 数据库列信息
     */
    @Data
    private static class DatabaseColumnInfo {

        /**
         * 列名称
         */
        private String columnName;

        /**
         * 列类型
         */
        private JdbcType jdbcType;

        /**
         * 是否主键
         */
        private boolean primaryKey;
    }

}
