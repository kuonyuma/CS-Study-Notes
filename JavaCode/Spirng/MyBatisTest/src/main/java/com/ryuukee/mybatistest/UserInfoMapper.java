package com.ryuukee.mybatistest;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserInfoMapper {
    //查找所有数据
    List<UserInfo> SelectAll();

    //练习foreach标签
    List<UserInfo>select2(@Param("idList") List<Integer> idList);

    //实现批量增加数据
    Integer insert4(@Param("uInfos") List<UserInfo> uInfos);

    //增加数据
    Integer insert(UserInfo bean);
    //更新数据
    Integer update(String username, String password, Integer age, Integer id);

    //删除数据
    Integer delete(Integer id);

    //练习动态sql
    Integer insert2(UserInfo bean);

    //练习trim
    Integer insert3(UserInfo bean);

    //练习where
    List<UserInfo> selectByCondition(UserInfo bean);

    //练习set标签
    Integer update2(Integer id,UserInfo bean);

    //练习include标签
    List<UserInfo> select5(UserInfo bean);

}
