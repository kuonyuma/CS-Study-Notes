package com.lyuke.springdidemo.Service;

import com.lyuke.springdidemo.Student.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Pirnt {
//
//    //属性注入
//    @Autowired
//    private Student s1;
//

    //构造方法注入

    private Student s2;
    public Pirnt(Student s){
        this.s2 = s;
    }

    public void print(){
        System.out.println(s2);
    }
}
