package com.ryuukee.learmybatis;


import com.ryuukee.learmybatis.mapper.UserInfoMapper;
import com.ryuukee.learmybatis.model.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

@SpringBootTest
class LearnMyBatisApplicationTests {

    // 自动注入我们刚刚写好的提货单（Mapper接口）
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Test
    void testQueryAllUser() {
        // 调用接口里的方法，去数据库拿数据
        List<UserInfo> userList = userInfoMapper.queryAllUser();
        // 把拿到的数据一条条打印到控制台上
        System.out.println("====== 准备开始打印数据库的数据 ======");
        for (UserInfo user : userList) {
            System.out.println(user);
        }
        System.out.println("====== 数据打印完毕 ======");
    }

    @Test
    void selectByAge() {
        UserInfo bean = userInfoMapper.SelectByAge(20);
        System.out.println(bean);
    }
    @Test
    void selectByName() {
        UserInfo bean = userInfoMapper.SelectByName("lisi");
        System.out.println(bean);
    }

    @Test
    void insert(){
        UserInfo bean = new UserInfo();
        bean.setAge(11);
        bean.setPassword("123");
        bean.setUsername("yuma");

        Integer row =  userInfoMapper.Insert(bean);
        System.out.println(row);
        System.out.println(bean.getId());
    }

    @Test
    void delete(){
        Integer num = userInfoMapper.Delete(12);
        System.out.println(num);
    }

}