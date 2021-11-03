package com.github.mybatis.statement.resolver;

import com.github.mybatis.MybatisExpandException;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
     * 数据库类型支持性的校验只需进行一次即可
     */
    private boolean alreadyCheck;

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
        List<DatabaseColumnInfo> databaseColumnInfos = obtainDatabaseColumnInfo(sqlSession, tableName);


        return tableMetaData;
    }

    /**
     * 判断数据库类型是否支持
     */
    private void checkDatabaseTypeSupported(SqlSession sqlSession) {
        if (!alreadyCheck) {
            try (Connection connection = sqlSession.getConfiguration()
                    .getEnvironment().getDataSource().getConnection()) {
                if (!connection.isClosed()) {
                    String databaseType = connection.getMetaData().getDatabaseProductName();
                    if (!(MySQL.equalsIgnoreCase(databaseType) || MariaDB.equalsIgnoreCase(databaseType))) {
                        throw new MybatisExpandException("This database type [" + databaseType + "] is not currently supported");
                    }
                    alreadyCheck = true;
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
                    databaseColumnInfo.setJdbcType(MysqlType.nameOf(resultSet.getString(2)).getJdbcType());
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
    static class DatabaseColumnInfo {

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


    /**
     * MysqlType与JdbcType的映射
     */
    public enum MysqlType {

        DECIMAL("decimal", JdbcType.DECIMAL),
        TINYINT("tinyint", JdbcType.TINYINT),
        BOOLEAN("boolean", JdbcType.BOOLEAN),
        SMALLINT("smallint", JdbcType.SMALLINT),
        INT("int", JdbcType.INTEGER),
        INTEGER("integer", JdbcType.INTEGER),
        FLOAT("float", JdbcType.FLOAT),
        REAL("real", JdbcType.REAL),
        DOUBLE("double", JdbcType.DOUBLE),
        TIMESTAMP("timestamp", JdbcType.TIMESTAMP),
        DATE("date", JdbcType.DATE),
        TIME("time", JdbcType.TIME),
        DATETIME("datetime", JdbcType.TIMESTAMP),
        BIGINT("bigint", JdbcType.BIGINT),
        MEDIUMINT("mediumint", JdbcType.INTEGER),
        YEAR("year", JdbcType.DATE),
        VARCHAR("varchar", JdbcType.VARCHAR),
        VARBINARY("varbinary", JdbcType.VARBINARY),
        BIT("bit", JdbcType.BIT),
        JSON("json", JdbcType.LONGVARCHAR),
        ENUM("enum", JdbcType.CHAR),
        SET("set", JdbcType.CHAR),
        TINYBLOB("tinyblob", JdbcType.VARBINARY),
        TINYTEXT("tinytext", JdbcType.VARCHAR),
        MEDIUMBLOB("mediumblob", JdbcType.LONGVARBINARY),
        MEDIUMTEXT("mediumtext", JdbcType.LONGVARCHAR),
        LONGBLOB("longblob", JdbcType.LONGVARBINARY),
        LONGTEXT("longtext", JdbcType.LONGVARCHAR),
        BLOB("blob", JdbcType.LONGVARBINARY),
        TEXT("text", JdbcType.LONGVARCHAR),
        CHAR("char", JdbcType.CHAR),
        BINARY("binary", JdbcType.BINARY),
        GEOMETRY("geometry", JdbcType.BINARY),
        LINESTRING("linestring", JdbcType.BINARY),
        MULTILINESTRING("multilinestring", JdbcType.BINARY),
        POINT("point", JdbcType.BINARY),
        MULTIPOINT("multipoint", JdbcType.BINARY),
        POLYGON("polygon", JdbcType.BINARY),
        MULTIPOLYGON("multipolygon", JdbcType.BINARY),
        GEOMETRY_COLLECTION("geometrycollection", JdbcType.BINARY),
        UNKNOWN("unknown", JdbcType.OTHER);

        private final String name;
        protected JdbcType jdbcType;

        MysqlType(String mysqlTypeName, JdbcType jdbcType) {
            this.name = mysqlTypeName;
            this.jdbcType = jdbcType;
        }

        public String getName() {
            return this.name;
        }

        public JdbcType getJdbcType() {
            return jdbcType;
        }

        public static MysqlType nameOf(String mysqlType) {
            Optional<MysqlType> mysqlTypeOptional = Arrays.stream(MysqlType.values())
                    .filter(data -> data.getName().equalsIgnoreCase(mysqlType)).findFirst();
            return mysqlTypeOptional.orElse(MysqlType.UNKNOWN);
        }
    }


}
