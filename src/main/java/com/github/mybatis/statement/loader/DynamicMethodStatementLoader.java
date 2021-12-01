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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.github.mybatis.MybatisExpandContext.KEYWORDS_ESCAPE_FUNCTION;
import static com.github.mybatis.MybatisExpandContext.underlineToHumpFunction;

/**
 * 动态方法名查询功能加载器
 *
 * @author echils
 */
@Slf4j
public class DynamicMethodStatementLoader extends AbstractExpandStatementLoader {

    private static final List<String> EXPRESSION_PREFIX_TEMPLATE = new ArrayList<>();
    private static Set<String> PART_SET;
    private static final String FIND_EXPRESSION_PREFIX = "findBy";
    private static final String SELECT_EXPRESSION_PREFIX = "selectBy";
    private static final String KEYWORD_TEMPLATE = "(%s)(?=(\\p{Lu}|\\P{InBASIC_LATIN}))";
    private static final Pattern DIRECTION_SPLIT = Pattern.compile("(.+?)(Asc|Desc)?$");
    private static final String FIELD_EXPRESSION_JOINT = ",";

    public DynamicMethodStatementLoader() {
        EXPRESSION_PREFIX_TEMPLATE.add(FIND_EXPRESSION_PREFIX);
        EXPRESSION_PREFIX_TEMPLATE.add(SELECT_EXPRESSION_PREFIX);
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
        List<SqlNode> sqlNodes = new ArrayList<>();
        TableMetaData tableMetaData = mappedMetaData.getTableMetaData();
        sqlNodes.add(new StaticTextSqlNode("SELECT " + mappedMetaData.getBaseColumnList() + " FROM "
                + KEYWORDS_ESCAPE_FUNCTION.apply(tableMetaData.getName()) + " WHERE 1=1 "));
        if (StringUtils.isNotBlank(mappedMetaData.getWhereClause())) {
            sqlNodes.add(new StaticTextSqlNode(" AND " + mappedMetaData.getWhereClause()));
        }
        sqlNodes.addAll(buildSqlNode(mappedMetaData));
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
        String methodPrefix = expressionPrefixOptional.get();
        methodName = methodName.substring(methodPrefix.length());
        Set<String> fieldSet = mappedMetaData.getTableMetaData().getColumnMetaDataList().stream()
                .map(ColumnMetaData::getFieldName).map(field -> underlineToHumpFunction.apply(field, true))
                .collect(Collectors.toSet());
        //关键字和属性值简单校验，如果存在属性和关键字相同的情况，不在本次校验范围,推迟到Statement构建时校验
        Set<String> orderSet = Arrays.stream(Order.values()).map(order -> order.value).collect(Collectors.toSet());
        if (PART_SET.stream().noneMatch(fieldSet::contains) && orderSet.stream().noneMatch(fieldSet::contains)) {
            for (String keyword : PART_SET) {
                methodName = methodName.replaceAll(keyword, FIELD_EXPRESSION_JOINT);
            }
            for (String order : orderSet) {
                if (methodName.endsWith(order)) {
                    methodName = methodName.substring(0, methodName.lastIndexOf(order));
                }
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
     * 根据方法名构建
     */
    private Collection<? extends SqlNode> buildSqlNode(MappedMetaData mappedMetaData) {
        List<SqlNode> sqlNodes = new ArrayList<>();
        Method mappedMethod = mappedMetaData.getMappedMethod();
        Map<String, ColumnMetaData> fieldMap = mappedMetaData.getTableMetaData().getColumnMetaDataList()
                .stream().collect(Collectors.toMap(columnMetaData -> underlineToHumpFunction.apply(
                        columnMetaData.getFieldName(), true), columnMetaData -> columnMetaData));
        Set<String> fieldSet = fieldMap.keySet();
        String methodName = mappedMethod.getName();
        methodName = methodName.substring(EXPRESSION_PREFIX_TEMPLATE
                .stream().filter(methodName::startsWith).findFirst().orElse("").length());
        Pattern pattern = Pattern.compile(String.format(KEYWORD_TEMPLATE, Part.ORDER_BY.value));
        String[] orderParamParts = pattern.split(methodName);
        if (orderParamParts.length > 2) {
            throw new IllegalArgumentException("OrderBy must not be used more than once in a method name!");
        }
        methodName = orderParamParts[0];
        pattern = Pattern.compile(String.format(KEYWORD_TEMPLATE, Part.OR.value));
        String[] orParamParts = pattern.split(methodName);
        for (String paramPart : orParamParts) {
            if (StringUtils.isNotBlank(paramPart)) {
                pattern = Pattern.compile(String.format(KEYWORD_TEMPLATE, Part.AND.value));
                String[] andParamPart = pattern.split(paramPart);
                for (String param : andParamPart) {
                    if (StringUtils.isNotBlank(param)) {
                        String property = extractProperty(param);
                        if (!fieldSet.contains(property)) {
                            throw new IllegalArgumentException("Invalid syntax because of " +
                                    "the entity class does not have this property[" + property + "]");
                        }
                        Part conditionPart = Part.EQUALS;
                        String condition = param.replace(property, "");
                        if (StringUtils.isNotBlank(condition)) {
                            Part part = Part.nameOf(condition);
                            if (part == null) {
                                throw new IllegalArgumentException("Invalid syntax because of " +
                                        "no support the condition [" + property + "]");
                            }
                            conditionPart = part;
                        }
                        sqlNodes.add(buildSqlNode(KEYWORDS_ESCAPE_FUNCTION
                                .apply(fieldMap.get(property).getColumnName()), conditionPart));
                    }
                }
            }
        }

        if (orderParamParts.length == 2) {
            String orderPart = orderParamParts[1];
            Matcher matcher = DIRECTION_SPLIT.matcher(orderPart);
            if (!matcher.find()) {
                throw new IllegalArgumentException("Invalid order syntax for part [" + orderPart + "]");
            }
            String propertyString = matcher.group(1);
            if (!fieldSet.contains(propertyString)) {
                throw new IllegalArgumentException("Invalid order syntax for part [" + orderPart + "]," +
                        "the entity class does not have this property[" + propertyString + "]");
            }
            sqlNodes.add(new StaticTextSqlNode(" Order by " + KEYWORDS_ESCAPE_FUNCTION
                    .apply(fieldMap.get(propertyString).getColumnName()) + " " + matcher.group(2)));
        }
        return sqlNodes;
    }

    /**
     * 根据关键字构建
     */
    private SqlNode buildSqlNode(String column, Part part) {

        switch (part) {

        }
        return null;
    }


    /**
     * 提取属性值
     */
    private String extractProperty(String part) {
        for (Part keyword : Part.values()) {
            if (part.endsWith(keyword.value)) {
                return part.substring(0, part.length() - keyword.value.length());
            }
        }
        return part;
    }

    /**
     * 表达式关键字
     */
    enum Part {

        AND("And"),
        OR("Or"),
        EQUALS("Equals", 1),
        NOT_EQUALS("NotEquals", 1),
        BETWEEN("Between", 2),
        NOT_BETWEEN("NotBetween", 2),
        LESS_THAN("LessThan", 1),
        LESS_THAN_EQUAL("LessThanEqual", 1),
        GREATER_THAN("GreaterThan", 1),
        GREATER_THAN_EQUAL("GreaterThanEqual", 1),
        IS_NULL("IsNull"),
        IS_NOT_NULL("IsNotNull"),
        IS_BLANK("IsBlank"),
        IS_NOT_BLANK("IsNotBlank"),
        LIKE("Like", 1),
        NOT_LIKE("NotLike", 1),
        STARTING_WITH("StartingWith", 1),
        ENDING_WITH("EndingWith", 1),
        IN("In", 1),
        NOT_IN("NotIn", 1),
        ORDER_BY("OrderBy", 1);

        public String value;

        public int argNum;

        Part(String value) {
            this(value, 0);
        }

        Part(String value, int argNum) {
            this.value = value;
            this.argNum = argNum;
        }

        public static Part nameOf(String value) {
            return Arrays.stream(Part.values()).filter(part
                    -> part.value.equals(value)).findFirst().orElseThrow(()
                    -> new MybatisExpandException("This version does not support this keyword [" + value + "]"));
        }

    }

    /**
     * 排序关键字
     */
    enum Order {

        ASC("Asc"),
        DESC("Desc");
        public String value;

        Order(String value) {
            this.value = value;
        }
    }


}
