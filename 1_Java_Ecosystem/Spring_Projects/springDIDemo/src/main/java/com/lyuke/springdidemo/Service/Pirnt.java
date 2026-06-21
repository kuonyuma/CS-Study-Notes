package com.lyuke.springdidemo.Service;

import com.lyuke.springdidemo.Student.Student;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class Pirnt {

//    private final Student s1;
//    @Autowired
//    public Pirnt(Student s1){
//        this.s1 = s1;
//    }


//    //构造方法注入
//
//    @Autowired
//    public Pirnt(Student s) {
//        this.s2 = s;
//
//    }
//    public Pirnt(){
//
//    }

//    //setter注入
//    private Student s2;
//    @Autowired
//    public void setS2(Student s2) {
//        this.s2 = s2;
//    }
//
//    @Qualifier("student2")
//    @Autowired
//    private Student s1;

    @Resource(name = "student2")
    private Student s1;
    public void print(){
        System.out.println(s1);
    }
}
