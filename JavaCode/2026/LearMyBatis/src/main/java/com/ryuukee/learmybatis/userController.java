package com.ryuukee.learmybatis;

import com.ryuukee.learmybatis.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userController")
public class userController {

    @Autowired
    private UserServer userServer;

    @RequestMapping("/selectByName")
    public UserInfo SelectByName(@RequestParam("user_name") String username){
        return userServer.SelectByName(username);
    }
}
