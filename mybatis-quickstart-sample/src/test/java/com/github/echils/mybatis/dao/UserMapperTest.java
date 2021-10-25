package com.github.echils.mybatis.dao;

import com.github.echils.mybatis.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

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
        user.setCreateTime(new Date());
        user.setSex("男");
        user.setLogical(true);
    }

    @Test
    public void testInsert() {
        userMapper.insert(user);
    }

}