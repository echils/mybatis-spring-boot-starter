package com.github.mybatis.statement.loader;

import com.github.mybatis.specification.DynamicMapper;
import com.github.mybatis.statement.metadata.ColumnMetaData;
import com.github.mybatis.statement.metadata.MappedMetaData;
import com.github.mybatis.statement.metadata.TableMetaData;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.*;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.mybatis.MybatisExpandContext.KEYWORDS_ESCAPE_FUNCTION;

/**
 * 动态统计功能加载器
 *
 * @author echils
 */
@Slf4j
public class DynamicCountStatementLoader extends AbstractExpandStatementLoader {

    /**
     * 拓展方法名
     */
    private static final String EXPAND_STATEMENT_METHOD
            = DynamicMapper.class.getName() + ".count";

    @Override
    SqlCommandType sqlCommandType() {
        return SqlCommandType.SELECT;
    }

    @Override
    SqlSource sqlSourceBuild(MappedMetaData mappedMetaData) {
        Configuration configuration =
                mappedMetaData.getMapperFactoryBean().getSqlSession().getConfiguration();
        TableMetaData tableMetaData = mappedMetaData.getTableMetaData();
        Map<String, String> columnFieldMap = tableMetaData.getColumnMetaDataList().stream()
                .collect(Collectors.toMap(ColumnMetaData::getColumnName, ColumnMetaData::getFieldName));
        List<SqlNode> sqlNodes = new ArrayList<>();
        sqlNodes.add(new StaticTextSqlNode("SELECT COUNT(1) FROM "
                + KEYWORDS_ESCAPE_FUNCTION.apply(tableMetaData.getName()) + " WHERE 1=1"));

//        sqlNodes.add(new IfSqlNode(new ForEachSqlNode(configuration, new MixedSqlNode(
//                new IfSqlNode(new StaticTextSqlNode("AND")))), "whereConditions" != null));

        return new DynamicSqlSource(configuration, new MixedSqlNode(sqlNodes));
    }

    @Override
    public boolean match(MappedMetaData mappedMetaData) {
        return mappedMetaData.getMappedMethod().toString().contains(EXPAND_STATEMENT_METHOD);
    }

}
