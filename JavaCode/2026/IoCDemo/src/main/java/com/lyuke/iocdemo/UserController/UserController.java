package com.lyuke.iocdemo.UserController;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/UserController")
@Controller
//@Configuration
@ResponseBody
public class UserController {

    @RequestMapping("/print")
    public String print() {
       return "使用 UserController";
    }
}
