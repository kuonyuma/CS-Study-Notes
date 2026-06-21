package com.lyuke.httpdemo.Login;

import jakarta.servlet.http.HttpSession;
import org.springframework.boot.web.server.servlet.Session;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Login")
public class Login {

    @RequestMapping("/input")
    public String inPut(HttpSession session){

        String keyval = (String)session.getAttribute("Loginkey");

        if(StringUtils.hasLength(keyval)){
            return "您好"+ keyval;
        }

        session.setAttribute("Loginkey","lisi");
        return "您已经初始化账号";
    }

}
