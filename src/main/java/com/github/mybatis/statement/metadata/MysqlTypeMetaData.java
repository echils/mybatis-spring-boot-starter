package com.github.mybatis.statement.metadata;

import org.apache.ibatis.type.JdbcType;

import java.util.Arrays;
import java.util.Optional;

/**
 * MysqlType与JdbcType的映射
 *
 * @author echils
 */
public enum MysqlTypeMetaData {

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

    MysqlTypeMetaData(String mysqlTypeName, JdbcType jdbcType) {
        this.name = mysqlTypeName;
        this.jdbcType = jdbcType;
    }

    public String getName() {
        return this.name;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public static MysqlTypeMetaData nameOf(String mysqlType) {
        Optional<MysqlTypeMetaData> mysqlTypeOptional = Arrays.stream(MysqlTypeMetaData.values())
                .filter(data -> data.getName().equalsIgnoreCase(mysqlType)).findFirst();
        return mysqlTypeOptional.orElse(MysqlTypeMetaData.UNKNOWN);
    }

}