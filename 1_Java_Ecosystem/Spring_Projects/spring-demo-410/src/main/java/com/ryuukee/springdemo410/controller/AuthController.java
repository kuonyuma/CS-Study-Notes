package com.ryuukee.springdemo410.controller;

import com.ryuukee.springdemo410.model.ApiResponse;
import com.ryuukee.springdemo410.model.LoginRequest;
import com.ryuukee.springdemo410.model.User;
import com.ryuukee.springdemo410.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证与会话管理控制器
 *
 * 核心演示点：
 * 1. HttpServletRequest 与 HttpServletResponse 的底层原生操作
 * 2. Session 的创建、写入、读取与销毁
 * 3. @SessionAttribute 注解的使用（自动从 Session 获取属性对象）
 * 4. Cookie 的设置与发送 (response.addCookie)
 * 5. @CookieValue 注解的使用（自动从客户端 Cookie 请求头中解析值）
 * 6. 自定义设置 HTTP 状态码 (response.setStatus) 与响应头 (response.setHeader)
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 1. 用户登录接口
     *
     * 注解与参数解析：
     * - @RequestBody: 接收 JSON 格式的用户名和密码
     * - HttpServletRequest: Servlet 原生请求对象，用于获取或创建 HttpSession
     * - HttpServletResponse: Servlet 原生响应对象，用于向客户端下发 Cookie、设置状态码与自定义响应头
     */
    @PostMapping("/login")
    public ApiResponse<User> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response) {

        // 验证用户凭据
        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (user == null) {
            // 设置 HTTP 状态码为 401 (Unauthorized)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return ApiResponse.error(401, "用户名或密码错误");
        }

        // ================= 1. Session 机制操作 =================
        // request.getSession(true) 表示如果当前请求没有对应的 Session 则新建一个
        // Tomcat 会自动在响应中添加 Set-Cookie: JSESSIONID=xxx
        HttpSession session = request.getSession(true);
        session.setAttribute("currentUser", user);

        // ================= 2. Cookie 机制操作 =================
        // 创建一个记录用户偏好主题的 Cookie
        Cookie themeCookie = new Cookie("user_theme", "dark");
        themeCookie.setPath("/"); // 设置 Cookie 生效的作用路径为全局根路径
        themeCookie.setMaxAge(7 * 24 * 60 * 60); // 设置有效期为 7 天 (单位：秒)
        themeCookie.setHttpOnly(false); // 允许前端脚本读取（若涉及安全 token 建议设为 true）
        response.addCookie(themeCookie); // 将 Cookie 添加到 HTTP Response Header 中

        // ================= 3. 自定义 Response Header =================
        response.setHeader("X-Auth-Status", "SUCCESS");
        response.setHeader("X-Server-Time", String.valueOf(System.currentTimeMillis()));

        return ApiResponse.success("登录成功，用户信息已写入 Session，偏好已写入 Cookie", user);
    }

    /**
     * 2. 获取当前登录用户信息
     *
     * 注解解析：
     * - @SessionAttribute(value = "currentUser", required = false):
     *   从当前的 HttpSession 中直接提取名为 "currentUser" 的属性值，并自动强转为 User 类型。
     *   如果 required = true (默认)，当 Session 中没有该属性时会抛出异常；
     *   设置 required = false 可以在未登录时优雅地返回 null，由业务代码进行友好提示。
     */
    @GetMapping("/current-user")
    public ApiResponse<User> getCurrentUser(
            @SessionAttribute(value = "currentUser", required = false) User currentUser,
            HttpServletResponse response) {

        if (currentUser == null) {
            // 未登录时设置状态码为 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return ApiResponse.error(401, "当前会话尚未登录，请先调用 /api/auth/login");
        }
        return ApiResponse.success("获取当前会话用户成功", currentUser);
    }

    /**
     * 3. 读取用户偏好（通过 Cookie）
     *
     * 注解解析：
     * - @CookieValue(value = "user_theme", defaultValue = "light"):
     *   自动从客户端请求头中的 Cookie 字段解析名为 "user_theme" 的 Cookie 值。
     *   若客户端未携带该 Cookie，则赋予默认值 "light"。
     */
    @GetMapping("/preference")
    public ApiResponse<Map<String, String>> getPreference(
            @CookieValue(value = "user_theme", defaultValue = "light") String theme) {

        Map<String, String> data = new HashMap<>();
        data.put("user_theme", theme);
        return ApiResponse.success("读取用户 Cookie 偏好成功", data);
    }

    /**
     * 4. 更新用户偏好 Cookie
     */
    @PostMapping("/preference")
    public ApiResponse<String> setPreference(
            @RequestParam("theme") String theme,
            HttpServletResponse response) {

        Cookie themeCookie = new Cookie("user_theme", theme);
        themeCookie.setPath("/");
        themeCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(themeCookie);

        return ApiResponse.success("偏好已更新为: " + theme, null);
    }

    /**
     * 5. 退出登录
     */
    @PostMapping("/logout")
    public ApiResponse<String> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        // 1. 销毁当前 Session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // 2. 清除客户端 Cookie (通过下发 maxAge 为 0 的同名 Cookie)
        Cookie clearCookie = new Cookie("user_theme", "");
        clearCookie.setPath("/");
        clearCookie.setMaxAge(0);
        response.addCookie(clearCookie);

        return ApiResponse.success("已成功退出登录，Session 已注销，Cookie 已清除", null);
    }
}
