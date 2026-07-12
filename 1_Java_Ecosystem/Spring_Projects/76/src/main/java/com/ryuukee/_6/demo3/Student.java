package com.ryuukee._6.demo3;

public class Student {

    private  String name;


    public Student(){}
    public Student(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }

    public void doWork(){
        System.out.println(this.name+"朗读课文");
    }

    public void setName(String name) {
        this.name = name;
    }
}
