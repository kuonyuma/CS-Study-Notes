package com.ryuukee.interpectordemo.Config;

import com.ryuukee.interpectordemo.Interpector.LoginInterpector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC 全局配置类 —— 将拦截器注册到 Spring MVC 的拦截器链中
 *
 * 实现 WebMvcConfigurer 接口并重写 addInterceptors 方法，
 * 即可控制哪些路径被拦截、哪些路径被放行。
 */
@Configuration // 告诉 Spring 这是一个配置类
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterpector loginInterpector; // 注入我们写的拦截器

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterpector)
                .addPathPatterns("/**")              // 拦截所有请求
                .excludePathPatterns("/Login/login-user", "/Login/login-user/**"); // 放行登录接口
    }
}