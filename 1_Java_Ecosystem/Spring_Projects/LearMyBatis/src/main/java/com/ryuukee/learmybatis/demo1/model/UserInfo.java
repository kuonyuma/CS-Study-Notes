package com.ryuukee.learmybatis.demo1.model;

import lombok.Data;

import java.util.Date;

// @Data 注解是 Lombok 提供的神兵利器！
// 加上它，你就不需要手动去写那些长长的 get()、set() 和 toString() 方法了，它在编译时会自动帮你生成。
@Data
public class UserInfo {
    private Integer id;
    private String username;
    private String password;
    private Integer age;
    private Integer gender;
    private String phone;
    private Integer deleteFlag;
    private Date createTime;
    private Date updateTime;
}