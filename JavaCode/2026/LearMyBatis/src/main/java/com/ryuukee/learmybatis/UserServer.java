package com.ryuukee.learmybatis;


import com.ryuukee.learmybatis.mapper.UserInfoMapper;
import com.ryuukee.learmybatis.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserServer {

    @Autowired
    private UserInfoMapper mapper;

    public UserInfo SelectByName(String name){
        return mapper.SelectByName(name);
    }


}
