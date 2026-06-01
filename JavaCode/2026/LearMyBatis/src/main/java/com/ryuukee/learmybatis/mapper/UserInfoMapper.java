package com.ryuukee.learmybatis.mapper;

import com.ryuukee.learmybatis.model.UserInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserInfoMapper {

    @Select("select * from user_info")
    public List<UserInfo> queryAllUser();

    @Select("select * from user_info where age = #{age}")
    public UserInfo SelectByAge(Integer age);

    @Select("select * from user_info where username = #{name}")
    public UserInfo SelectByName(String name);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into user_info(username,`password`,age)" +
            "values(#{username},#{password},#{age})")
    Integer Insert(UserInfo bean);


    @Delete("delete from user_info where id = #{id}")
    Integer Delete(Integer id);
}

