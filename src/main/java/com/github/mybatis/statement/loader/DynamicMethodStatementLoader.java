package com.github.mybatis.statement.loader;

import com.github.mybatis.MybatisExpandException;
import com.github.mybatis.statement.metadata.ColumnMetaData;
import com.github.mybatis.statement.metadata.MappedMetaData;
import com.github.mybatis.statement.metadata.TableMetaData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.mybatis.MybatisExpandContext.underlineToHumpFunction;

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
    private static final List<String> EXPRESSION_PREFIX_TEMPLATE = new ArrayList<>();
    private static final List<String> EXPRESSION_ORDER_TEMPLATE = new ArrayList<>();
    private static Set<String> PART_SET;
    private static final String ORDER_ASC_PATTERN = "(?<=OrderBy).*?(?=(Asc))";
    private static final String ORDER_DESC_PATTERN = "(?<=OrderBy).*?(?=(Desc))";
    private static final String FIND_EXPRESSION_PREFIX = "findBy";
    private static final String SELECT_EXPRESSION_PREFIX = "selectBy";
    private static final String FIELD_EXPRESSION_JOINT = ",";

    public DynamicMethodStatementLoader() {
        EXPRESSION_PREFIX_TEMPLATE.add(FIND_EXPRESSION_PREFIX);
        EXPRESSION_PREFIX_TEMPLATE.add(SELECT_EXPRESSION_PREFIX);
        EXPRESSION_ORDER_TEMPLATE.add(ORDER_ASC_PATTERN);
        EXPRESSION_ORDER_TEMPLATE.add(ORDER_DESC_PATTERN);
        PART_SET = Collections.unmodifiableSet(Arrays
                .stream(Part.values()).map(part -> part.value).collect(Collectors.toSet()));
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
        Method mappedMethod = mappedMetaData.getMappedMethod();
        String methodName = mappedMethod.getName();
        //方法名前缀校验
        Optional<String> expressionPrefixOptional
                = EXPRESSION_PREFIX_TEMPLATE.stream().filter(methodName::startsWith).findFirst();
        if (!expressionPrefixOptional.isPresent()) {
            log.debug("The mapped interface's method [{}] does not meet the "
                    + "requirements for expanding the syntax tree,because the method name"
                    + " expand statement must start with [" + FIND_EXPRESSION_PREFIX + " or "
                    + SELECT_EXPRESSION_PREFIX + "]", mappedMetaData.getMappedStatementId());
            return false;
        }
        //关键字和属性值校验
        String methodPrefix = expressionPrefixOptional.get();
        methodName = methodName.substring(methodPrefix.length());
        Set<String> fieldSet = mappedMetaData.getTableMetaData().getColumnMetaDataList().stream()
                .map(ColumnMetaData::getFieldName).map(field -> underlineToHumpFunction.apply(field, true))
                .collect(Collectors.toSet());

        if (PART_SET.stream().anyMatch(fieldSet::contains)) {
            Map<String, Part> fieldMap = parseSyntaxTree(methodName, fieldSet);

        } else {
            for (String keyword : PART_SET) {
                methodName = methodName.replaceAll(keyword, FIELD_EXPRESSION_JOINT);
            }
            Optional<String> illegalFieldOptional = Arrays.stream(methodName
                    .split(FIELD_EXPRESSION_JOINT)).filter(key -> !fieldSet.contains(key)).findFirst();
            if (illegalFieldOptional.isPresent()) {
                log.debug("The mapped interface's method [{}] does not meet the "
                        + "requirements for expanding the syntax tree,because the method contains " +
                        "illegal field [{}]", mappedMetaData.getMappedStatementId(), illegalFieldOptional.get());
                return false;
            }
        }
        return true;
    }

    /**
     * 解析方法名
     */
    private Map<String, Part> parseSyntaxTree(String methodName, Set<String> fieldSet) {
        Map<String, Part> map = new HashMap<>();
        Iterator<String> iterator = fieldSet.iterator();
        String key = null;
        while (iterator.hasNext()) {
            String field = iterator.next();
            if (methodName.startsWith(field)) {
                key = field;
                break;
            }
        }
        if (StringUtils.isNotBlank(key)) {
            methodName = methodName.substring(methodName.indexOf(key));
        }
        for (String part : PART_SET) {
            if (methodName.startsWith(part)) {

            }
        }


        return map;
    }


    /**
     * 表达式关键字
     */
    enum Part {

        AND("And"),
        OR("Or"),
        EQUALS("Equals"),
        NOT_EQUALS("NotEquals"),
        BETWEEN("Between"),
        NOT_BETWEEN("NotBetween"),
        LESS_THAN("LessThan"),
        LESS_THAN_EQUAL("LessThanEqual"),
        GREATER_THAN("GreaterThan"),
        GREATER_THAN_EQUAL("GreaterThanEqual"),
        IS_NULL("IsNull"),
        IS_NOT_NULL("IsNotNull"),
        IS_BLANK("IsBlank"),
        IS_NOT_BLANK("IsNotBlank"),
        LIKE("Like"),
        NOT_LIKE("NotLike"),
        STARTING_WITH("StartingWith"),
        ENDING_WITH("EndingWith"),
        IN("In"),
        NOT_IN("NotIn"),
        ORDER_BY("OrderBy");

        public String value;

        Part(String value) {
            this.value = value;
        }

        public Part nameOf(String value) {
            return Arrays.stream(Part.values()).filter(part
                    -> part.value.equals(value)).findFirst().orElseThrow(()
                    -> new MybatisExpandException("This version does not support this keyword [" + value + "]"));
        }

    }


}
