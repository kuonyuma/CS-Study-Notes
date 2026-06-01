package com.ryuukee.learmybatis.xmltest;

import com.ryuukee.learmybatis.demo1.model.UserInfo;
import com.ryuukee.learmybatis.demo2.LearMyBatisApplication;
import com.ryuukee.learmybatis.demo2.UserInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = LearMyBatisApplication.class)
public class test {

    @Autowired
    private UserInfoMapper userInfoMapper;
    
    @Test
    void SelectAll(){
        List<UserInfo> users = userInfoMapper.SelectAll();
        for (UserInfo user : users) {
            System.out.println(user);
        }
    }
}
