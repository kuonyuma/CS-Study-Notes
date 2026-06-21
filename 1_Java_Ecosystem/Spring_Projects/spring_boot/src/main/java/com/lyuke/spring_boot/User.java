package com.lyuke.spring_boot;
public class User {
    String name;

    Integer age;

    int denger;

    public void setDenger(int denger) {
        this.denger = denger;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getDenger() {
        return denger;
    }

    public Integer getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", denger=" + denger +
                '}';
    }
}

