package com.lyuke.logindemo;

import jakarta.servlet.http.HttpSession;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/LoginClass")
public class Login {//检验登录界面

    @RequestMapping("/Login")
    public boolean judge(String UserName, String UserPassWord, HttpSession session){

        if("admin".equals(UserName) && "admin".equals(UserPassWord)){
            //登录成功后将用户名储存在session中
            session.setAttribute("UserName",UserName);
            return true;
        }
        return false;
    }

    @RequestMapping("/GetSession")
    public String GetSession(HttpSession session){
        String userName = (String)session.getAttribute("UserName");
        if(StringUtils.hasLength(userName))
            return userName;
        return " ";
    }


}
