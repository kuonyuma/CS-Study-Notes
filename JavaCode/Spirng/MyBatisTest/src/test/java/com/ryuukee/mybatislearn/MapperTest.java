package com.ryuukee.mybatislearn;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class MapperTest {

   @Autowired
    private MapperStu mapperstu;

    @Test
    void select() {
        //准备一个容器
        List<StudentInfo> infos;
        infos = mapperstu.select();
        System.out.println(infos);
    }

    @Test
    void delete() {
        Integer row = mapperstu.delete(1);
        System.out.println(row);
    }

    @Test
    void update(){
        mapperstu.update("true_or_false","李四");
    }

    @Test
    void insert(){
        StudentInfo bean = new StudentInfo();
        bean.setName("ryuukee");
        bean.setPassword("1111");
        bean.setGender("woman");
        mapperstu.insert(bean);
    }
}