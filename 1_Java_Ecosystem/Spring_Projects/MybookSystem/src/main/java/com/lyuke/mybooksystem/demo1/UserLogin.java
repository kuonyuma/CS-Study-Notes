package com.lyuke.mybooksystem.demo1;


import jakarta.servlet.http.HttpSession;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/User")
public class UserLogin {

    //登录验证
    @RequestMapping("Login")
    public boolean Login(String userName, String userPass, HttpSession session){
        if("admin".equals(userName)&&
            "admin".equals(userPass)){
            session.setAttribute("userName",userName);
            return true;
        }
        return false;
    }

}
