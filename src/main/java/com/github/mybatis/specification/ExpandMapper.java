package com.github.mybatis.specification;

/**
 * 拓展功能标记接口，继承该接口的映射器将会自动支持类似JPA根据方法名查询的功能。
 * 遗憾的是并没有JPA那样的智能提示 ┐(─__─)┌
 * 命名规则参考文档 @see <href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation">
 *
 * @param <T> 表对应的类
 * @author echils
 */
public interface ExpandMapper<T> {

}
