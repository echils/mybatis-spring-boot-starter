package com.github.mybatis.statement.loader;

import com.github.mybatis.MybatisExpandException;
import com.github.mybatis.statement.metadata.ColumnMetaData;
import com.github.mybatis.statement.metadata.MappedMetaData;
import com.github.mybatis.statement.metadata.TableMetaData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.*;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.github.mybatis.MybatisExpandContext.*;

/**
 * 动态方法名查询功能加载器
 *
 * @author echils
 */
@Slf4j
public class DynamicMethodStatementLoader extends AbstractExpandStatementLoader {

    private static final List<String> EXPRESSION_PREFIX_TEMPLATE = new ArrayList<>();
    private static final String FIND_EXPRESSION_PREFIX = "findBy";
    private static final String SELECT_EXPRESSION_PREFIX = "selectBy";
    private static final String KEYWORD_TEMPLATE = "(%s)(?=(\\p{Lu}|\\P{InBASIC_LATIN}))";
    private static final Pattern DIRECTION_SPLIT = Pattern.compile("(.+?)(Asc|Desc)?$");
    private static final String FIELD_EXPRESSION_JOINT = ",";

    public DynamicMethodStatementLoader() {
        EXPRESSION_PREFIX_TEMPLATE.add(FIND_EXPRESSION_PREFIX);
        EXPRESSION_PREFIX_TEMPLATE.add(SELECT_EXPRESSION_PREFIX);
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
        List<ColumnMetaData> columnMetaDataList = tableMetaData.getColumnMetaDataList();
        columnMetaDataList.stream().filter(ColumnMetaData::isLogical).forEach(columnMetaData ->
                sqlNodes.add(new StaticTextSqlNode(" AND " + columnMetaData.getColumnName() + "=" + columnMetaData.getExistValue())));
        sqlNodes.addAll(buildSqlNode(configuration, mappedMetaData));

        return new DynamicSqlSource(configuration, new MixedSqlNode(sqlNodes));
    }

    @Override
    public boolean match(MappedMetaData mappedMetaData) {
        //方法名前缀校验
        Optional<String> expressionPrefixOptional = EXPRESSION_PREFIX_TEMPLATE.stream()
                .filter(mappedMetaData.getMappedMethod().getName()::startsWith).findFirst();
        if (expressionPrefixOptional.isPresent()) {
            return true;
        }
        log.debug("The mapped interface's method [{}] does not meet the "
                + "requirements for expanding the syntax tree,because the method name"
                + " expand statement must start with [" + FIND_EXPRESSION_PREFIX + " or "
                + SELECT_EXPRESSION_PREFIX + "]", mappedMetaData.getMappedStatementId());
        return false;
    }

    /**
     * 根据方法名构建
     */
    private Collection<? extends SqlNode> buildSqlNode(Configuration configuration, MappedMetaData mappedMetaData) {

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
        int paramIndex = 0;
        for (String paramPart : orParamParts) {
            if (StringUtils.isNotBlank(paramPart)) {
                pattern = Pattern.compile(String.format(KEYWORD_TEMPLATE, Part.AND.value));
                String[] andParamPart = pattern.split(paramPart);
                for (String param : andParamPart) {
                    if (StringUtils.isNotBlank(param)) {
                        String property = extractProperty(param);
                        if (!fieldSet.contains(property)) {
                            throw new IllegalArgumentException("Invalid syntax of " +
                                    "the method [" + mappedMethod.getDeclaringClass().getName() + "." + mappedMethod.getName() + "] because the param [" + property + "] is illegal");
                        }
                        Part conditionPart = Part.EQUALS;
                        String condition = param.substring(property.length());
                        if (StringUtils.isNotBlank(condition)) {
                            Part part = Part.nameOf(condition);
                            if (part == null) {
                                throw new IllegalArgumentException("Invalid syntax of " +
                                        "the method [" + mappedMethod.getDeclaringClass().getName() + "." + mappedMethod.getName() + "] because no support the condition [" + property + "]");
                            }
                            conditionPart = part;
                        }
                        if (conditionPart.argNum == 0) {
                            sqlNodes.add(new StaticTextSqlNode(" And " + KEYWORDS_ESCAPE_FUNCTION
                                    .apply(fieldMap.get(property).getColumnName()) + conditionPart.expression));
                        } else if (conditionPart.argNum == 1) {
                            switch (conditionPart) {
                                case EQUALS:
                                case NOT_EQUALS:
                                case LIKE:
                                case NOT_LIKE:
                                case STARTING_WITH:
                                case ENDING_WITH:
                                case LESS_THAN:
                                case LESS_THAN_EQUAL:
                                case GREATER_THAN:
                                case GREATER_THAN_EQUAL:
                                    sqlNodes.add(new StaticTextSqlNode(" And " + KEYWORDS_ESCAPE_FUNCTION
                                            .apply(fieldMap.get(property).getColumnName())
                                            + MessageFormat.format(conditionPart.expression, "#{param" + ++paramIndex + "}")));
                                    break;
                                case IN:
                                case NOT_IN:
                                    Class<?> paramType = mappedMethod.getParameterTypes()[paramIndex];
                                    if (!paramType.isArray() && !Collection.class.isAssignableFrom(paramType)) {
                                        throw new IllegalArgumentException("Invalid syntax of " +
                                                "the method [" + mappedMethod.getDeclaringClass().getName() + "." + mappedMethod.getName() + "] because the parameter" +
                                                "must be array or collection when condition corresponding to [In or NotIn]");
                                    }
                                    String paramItem = mappedMethod.getParameterCount() > 1
                                            ? "param" + ++paramIndex : paramType.isArray() ? "array" : "collection";
                                    sqlNodes.add(new IfSqlNode(new MixedSqlNode(Arrays.asList(
                                            new StaticTextSqlNode(" And " + KEYWORDS_ESCAPE_FUNCTION
                                                    .apply(fieldMap.get(property).getColumnName()) + conditionPart.expression),
                                            new ForEachSqlNode(configuration, new StaticTextSqlNode("#{item}"), paramItem,
                                                    null, "item", "(", ")", ","))),
                                            String.format(MYBATIS_COLLECTION_EXPRESSION, paramItem, paramItem)));
                                    break;
                            }

                        } else if (conditionPart.argNum == 2) {
                            sqlNodes.add(new StaticTextSqlNode(" And " + KEYWORDS_ESCAPE_FUNCTION
                                    .apply(fieldMap.get(property).getColumnName())
                                    + MessageFormat.format(conditionPart.expression,
                                    "#{param" + ++paramIndex + "}", "#{param" + ++paramIndex + "}")));
                        }
                    }
                }
            }
        }

        if (orderParamParts.length == 2) {
            String orderPart = orderParamParts[1];
            pattern = Pattern.compile(String.format(KEYWORD_TEMPLATE, Part.AND.value));
            String[] andParamPart = pattern.split(orderPart);
            StringBuilder sqlNode = new StringBuilder(Part.ORDER_BY.expression);
            for (String orderParam : andParamPart) {
                Matcher matcher = DIRECTION_SPLIT.matcher(orderParam);
                if (!matcher.find()) {
                    throw new IllegalArgumentException("Invalid syntax of " +
                            "the method [" + mappedMethod.getDeclaringClass().getName() + "." + mappedMethod.getName() + "] because order syntax [" + orderPart + "] is illegal");
                }
                String propertyString = matcher.group(1);
                if (!fieldSet.contains(propertyString)) {
                    throw new IllegalArgumentException("Invalid syntax of " +
                            "the method [" + mappedMethod.getDeclaringClass().getName() + "." + mappedMethod.getName() + "] because order param [" + orderPart + "] is illegal");
                }
                sqlNode.append(KEYWORDS_ESCAPE_FUNCTION.apply(fieldMap
                        .get(propertyString).getColumnName())).append(" ").append(matcher.group(2)).append(FIELD_EXPRESSION_JOINT);
            }
            sqlNode.deleteCharAt(sqlNode.length() - 1);
            sqlNodes.add(new StaticTextSqlNode(sqlNode.toString()));
        }
        return sqlNodes;
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
        EQUALS("Equals", 1, " = {0} "),
        NOT_EQUALS("NotEquals", 1, " != {0} "),
        BETWEEN("Between", 2, " BETWEEN {0} AND {1} "),
        NOT_BETWEEN("NotBetween", 2, " NOT BETWEEN {0} AND {1} "),
        LESS_THAN("LessThan", 1, " < {0} "),
        LESS_THAN_EQUAL("LessThanEqual", 1, " <= {0} "),
        GREATER_THAN("GreaterThan", 1, " > {0} "),
        GREATER_THAN_EQUAL("GreaterThanEqual", 1, " >= {0} "),
        IS_NULL("IsNull", " IS NULL "),
        IS_NOT_NULL("IsNotNull", " IS NOT NULL "),
        IS_BLANK("IsBlank", " ='' "),
        IS_NOT_BLANK("IsNotBlank", " !='' "),
        LIKE("Like", 1, " LIKE CONCAT(\"%\",{0} ,\"%\")"),
        NOT_LIKE("NotLike", 1, " NOT LIKE CONCAT(\"%\",{0} ,\"%\")"),
        STARTING_WITH("StartingWith", 1, " LIKE CONCAT({0} ,\"%\") "),
        ENDING_WITH("EndingWith", 1, " LIKE CONCAT(\"%\",{0})"),
        IN("In", 1, " IN "),
        NOT_IN("NotIn", 1, " NOT IN "),
        ORDER_BY("OrderBy", 1, " ORDER BY ");

        public String value;

        public int argNum;

        public String expression;

        Part(String value) {
            this(value, 0, "");
        }

        Part(String value, String expression) {
            this(value, 0, expression);
        }

        Part(String value, int argNum, String expression) {
            this.value = value;
            this.argNum = argNum;
            this.expression = expression;
        }

        public static Part nameOf(String value) {
            return Arrays.stream(Part.values()).filter(part
                    -> part.value.equals(value)).findFirst().orElseThrow(()
                    -> new MybatisExpandException("This version does not support this keyword [" + value + "]"));
        }

    }


}
