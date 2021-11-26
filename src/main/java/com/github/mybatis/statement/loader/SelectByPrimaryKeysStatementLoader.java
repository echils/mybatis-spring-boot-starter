package com.github.mybatis.statement.loader;

import com.github.mybatis.specification.SpecificationMapper;
import com.github.mybatis.statement.metadata.ColumnMetaData;
import com.github.mybatis.statement.metadata.MappedMetaData;
import com.github.mybatis.statement.metadata.TableMetaData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.*;
import org.apache.ibatis.session.Configuration;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.mybatis.MybatisExpandContext.*;

/**
 * 通过主键批量查询功能加载器
 *
 * @author echils
 */
@Slf4j
public class SelectByPrimaryKeysStatementLoader extends AbstractExpandStatementLoader {

    /**
     * 拓展方法名
     */
    private static final String EXPAND_STATEMENT_METHOD
            = SpecificationMapper.class.getName() + ".selectByPrimaryKeys";

    @Override
    SqlCommandType sqlCommandType() {
        return SqlCommandType.SELECT;
    }

    @Override
    SqlSource sqlSourceBuild(MappedMetaData mappedMetaData) {
        Configuration configuration =
                mappedMetaData.getMapperFactoryBean().getSqlSession().getConfiguration();
        TableMetaData tableMetaData = mappedMetaData.getTableMetaData();
        List<SqlNode> sqlNodes = new LinkedList<>();
        sqlNodes.add(new StaticTextSqlNode("SELECT "));
        sqlNodes.add(new StaticTextSqlNode(mappedMetaData.getBaseColumnList()));
        sqlNodes.add(new StaticTextSqlNode(" FROM "
                + KEYWORDS_ESCAPE_FUNCTION.apply(tableMetaData.getName())));
        sqlNodes.add(new StaticTextSqlNode(" WHERE 1=1"));
        if (StringUtils.isNotBlank(mappedMetaData.getWhereClause())) {
            sqlNodes.add(new StaticTextSqlNode(" AND " + mappedMetaData.getWhereClause()));
        }
        List<ColumnMetaData> primaryKeyColumnDataList = tableMetaData.getColumnMetaDataList()
                .stream().filter(ColumnMetaData::isPrimaryKey).collect(Collectors.toList());
        //联合主键
        if (primaryKeyColumnDataList.size() > UNIQUE_PRIMARY_KEY_INDEX) {
            List<String> columnList = primaryKeyColumnDataList.stream().map(columnMetaData
                    -> MYBATIS_FOREACH_PARAM + "." + KEYWORDS_ESCAPE_FUNCTION.apply(columnMetaData.getColumnName()))
                    .collect(Collectors.toList());
            String condition = String.join(" AND ", columnList);
            sqlNodes.add(new ForEachSqlNode(configuration, new StaticTextSqlNode(" AND " + condition),
                    "collection", "index", MYBATIS_FOREACH_PARAM,
                    "(", ")", " OR "));
        } else {
        //唯一主键
            ColumnMetaData columnMetaData = primaryKeyColumnDataList.get(0);
            sqlNodes.add(new StaticTextSqlNode(" AND " + KEYWORDS_ESCAPE_FUNCTION
                    .apply(columnMetaData.getColumnName()) + " IN "));
            sqlNodes.add(new ForEachSqlNode(configuration, new StaticTextSqlNode("#{item}"),
                    "collection", null, MYBATIS_FOREACH_PARAM,
                    "(", ")", ","));
        }
        return new DynamicSqlSource(configuration, new MixedSqlNode(sqlNodes));
    }

    @Override
    public boolean match(MappedMetaData mappedMetaData) {
        return mappedMetaData.getMappedMethod().toString().contains(EXPAND_STATEMENT_METHOD);
    }
}