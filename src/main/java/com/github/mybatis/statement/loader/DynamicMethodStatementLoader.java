package com.github.mybatis.statement.loader;

import com.github.mybatis.statement.metadata.MappedMetaData;
import com.github.mybatis.statement.metadata.TableMetaData;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 动态方法名查询功能加载器
 *
 * @author echils
 */
@Slf4j
public class DynamicMethodStatementLoader extends AbstractExpandStatementLoader {

    /**
     * 支持的表达式前缀
     */
    private static final List<String> EXPRESSION_PREFIX_LIST = new ArrayList<>();

    private static final String FIND_EXPRESSION_PREFIX = "findBy";
    private static final String SELECT_EXPRESSION_PREFIX = "selectBy";

    public DynamicMethodStatementLoader() {
        EXPRESSION_PREFIX_LIST.add(FIND_EXPRESSION_PREFIX);
        EXPRESSION_PREFIX_LIST.add(SELECT_EXPRESSION_PREFIX);
    }

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
        sqlNodes.add(new StaticTextSqlNode("select * from " + tableMetaData.getName()));
        return new DynamicSqlSource(configuration, new MixedSqlNode(sqlNodes));
    }

    @Override
    public boolean match(MappedMetaData mappedMetaData) {
        return methodValidate(mappedMetaData.getMappedMethod());
    }


    /**
     * 判断方法是否
     *
     * @param method 动态方法
     */
    private boolean methodValidate(Method method) {


        return false;
    }




}
