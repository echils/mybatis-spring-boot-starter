package com.github.echils.mybatis.dao;

import com.github.echils.mybatis.model.User;
import com.github.mybatis.specification.condition.DynamicParam;
import com.github.mybatis.specification.condition.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * UserMapperTest
 *
 * @author echils
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    private User user;

    @Before
    public void test() {
        user = new User();
        user.setId("666");
        user.setName("echils");
        user.setAge(25);
        user.setSex("男");
        user.setLogical(true);
    }

//    @Test
//    public void testInsert() {
//        userMapper.insert(user);
//    }

//    @Test
//    public void insertSelective() {
//        userMapper.insertSelective(user);
//    }

//    @Test
//    public void insertBatch() {
//        User user2 = new User();
//        user2.setId("777");
//        user2.setName("echils");
//        user2.setAge(24);
//        user2.setSex("女");
//        user2.setLogical(true);
//        userMapper.insertBatch(Arrays.asList(user2, user));
//    }

//    @Test
//    public void selectByPrimaryKey() {
//        System.out.println(userMapper.selectByPrimaryKey("666"));
//    }

//    @Test
//    public void selectByPrimaryKeys() {
//        System.out.println(userMapper.selectByPrimaryKeys(Arrays.asList("666", "777")));
//    }


//    @Test
//    public void deleteByPrimaryKey() {
//        System.out.println(userMapper.deleteByPrimaryKey("777"));
//    }

//    @Test
//    public void deleteByPrimaryKeys() {
//        System.out.println(userMapper.deleteByPrimaryKeys(Arrays.asList("666", "777")));
//    }

//    @Test
//    public void existByPrimaryKey() {
//        System.out.println(userMapper.existByPrimaryKey("666"));
//    }

//    @Test
//    public void update() {
//        User user2 = new User();
//        user2.setId("666");
//        user2.setName("echils");
//        user2.setAge(24);
//        user2.setSex("女");
//        user2.setCreateTime(new Date());
//        user2.setLogical(true);
//        userMapper.update(user2);
//    }

//    @Test
//    public void updateSelective() {
//        User user2 = new User();
//        user2.setId("666");
//        user2.setAge(18);
//        user2.setSex("女");
//        userMapper.updateSelective(user2);
//    }

//    @Test
//    public void updateSelectiveBatch() {
//        User user2 = new User();
//        user2.setAge(18);
//        user2.setCreateTime(new Date());
//        userMapper.updateSelectiveBatch(Arrays.asList("666", "777"), user2);
//    }

//    @Test
//    public void updateBatch() {
//        User user2 = new User();
//        user2.setId("777");
//        user2.setName("echils");
//        user2.setAge(24);
//        user2.setSex("女");
//        user2.setCreateTime(new Date());
//        user2.setLogical(true);
//
//        user.setAge(20);
//        user.setCreateTime(new Date());
//        user.setLogical(false);
//        userMapper.updateBatch(Arrays.asList(user2, user));
//    }

    @Test
    public void testFindAll() {
        //查询全部
        System.out.println(userMapper.findAll());

        //动态查询
        DynamicParam dynamicParam = new DynamicParam();
        dynamicParam.where(condition -> condition.between("age", 21, 24));
        dynamicParam.where(condition -> condition.rightLike("name", "ec"), DynamicParam.Joint.OR);
        dynamicParam.where(condition -> condition.gt("create_time", "2021-01-01"));
        dynamicParam.groupBy("id", "name");
        dynamicParam.having(condition -> condition.eq("logical", "1"));
        dynamicParam.order("id", Order.Rule.ASC);
        dynamicParam.order(new Order("create_time", Order.Rule.ASC));
        dynamicParam.page(0, 1);
        System.out.println(userMapper.findAll(dynamicParam));
    }

}