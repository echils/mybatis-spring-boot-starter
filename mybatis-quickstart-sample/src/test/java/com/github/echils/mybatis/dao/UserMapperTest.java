package com.github.echils.mybatis.dao;

import com.github.echils.mybatis.model.User;
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

    @Test
    public void selectByPrimaryKey() {
        System.out.println(userMapper.selectByPrimaryKey("666"));
    }

}