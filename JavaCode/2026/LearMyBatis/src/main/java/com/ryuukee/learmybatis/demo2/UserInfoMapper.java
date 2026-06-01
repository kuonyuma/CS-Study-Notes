package com.ryuukee.learmybatis.demo2;

import com.ryuukee.learmybatis.demo1.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserInfoMapper {
    List<UserInfo> SelectAll();

}
