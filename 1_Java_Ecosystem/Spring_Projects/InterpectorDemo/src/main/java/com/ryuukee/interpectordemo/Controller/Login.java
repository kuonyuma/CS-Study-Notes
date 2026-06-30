package com.ryuukee.interpectordemo.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录控制器
 * 提供两个接口：
 *   POST /Login/loginUser  —— 用户登录（拦截器会放行此接口）
 *   GET  /Login/userInfo   —— 需要登录才能访问（拦截器会检查 Session）
 */
@RestController
@RequestMapping("/Login")
public class Login {

    // 模拟的合法账号（学习用，省去数据库）
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "123456";

    /**
     * 登录接口
     * 参数：username、password（通过表单或请求参数传入）
     * 登录成功后，将用户名写入 Session，作为"已登录"的凭证
     */
    @RequestMapping("/login-user")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            HttpSession session) {

        // 1. 校验用户名和密码
        if (VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password)) {

            // 2. 登录成功 —— 把用户信息存入 Session
            //    拦截器将依靠这个 key 来判断用户是否已登录
            session.setAttribute("loginUser", username);

            return "登录成功！欢迎你，" + username;
        }

        // 3. 校验失败
        return "用户名或密码错误，登录失败！";
    }

    /**
     * 需要登录才能访问的接口（演示拦截器效果）
     * 若未登录直接访问，拦截器会将请求拦下来，不会到达这里
     */
    @RequestMapping("/user-info")
    public String userInfo(HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        return "当前登录用户：" + loginUser;
    }
}