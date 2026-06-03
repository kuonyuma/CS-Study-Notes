package com.ryuukee.mybatistest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MyBatisTestApplicationTests {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void SelectAll(){
        List<UserInfo> userInfos = userInfoMapper.SelectAll();

        for(UserInfo e:userInfos){
            System.out.println(e);
        }
    }
    @Test
    void selectByCondition(){
        UserInfo bean = new UserInfo();
//        bean.setUsername("zhangsan");
        bean.setGender(1);
        List<UserInfo> userInfos = userInfoMapper.selectByCondition(bean);
        System.out.println("===============================");
        for (UserInfo e :userInfos)
            System.out.println(e);
        System.out.println("=====================================");
    }

    @Test
    void insert(){
        UserInfo bean = new UserInfo();
        bean.setUsername("eqweqewe");
        bean.setPassword("12331231231");
        bean.setAge(18);        // age 在数据库是 NOT NULL，必须赋值
        bean.setGender(1);

        Integer num = userInfoMapper.insert(bean);
        System.out.println("受影响行数：" + num);
    }
    @Test
    void update(){
        userInfoMapper.update("更新了一条数据","311312313",0,1);
    }

    @Test
    void delete(){
         userInfoMapper.delete(1);
    }

    @Test
    void insert2() {
        UserInfo bean = new UserInfo();
        bean.setUsername("username18");
        bean.setPassword("pass18");
        bean.setAge(20);
//        bean.setGender(1);

        userInfoMapper.insert2(bean);
    }
    @Test
    void insert3() {
        UserInfo bean = new UserInfo();
        bean.setUsername("username18");
        bean.setPassword("pass18");
        bean.setAge(20);
        bean.setGender(1);

        userInfoMapper.insert3(bean);
    }

}
