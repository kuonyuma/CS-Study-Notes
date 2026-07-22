package com.ryuukee.springprinciple.demo1;

public class Student {

    private String name;

    public Student(){
    }

    public void doWork(){
        System.out.println(this.name + "正在写作业");
    }
    public void setName(String name){
        this.name = name;
    }
    public int getObjectHash() {
        return System.identityHashCode(this);
    }
}
