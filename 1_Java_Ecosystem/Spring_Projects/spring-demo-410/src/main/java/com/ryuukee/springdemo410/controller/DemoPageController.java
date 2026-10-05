package com.ryuukee.springdemo410.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 经典 Spring MVC 页面/视图控制器演示
 *
 * 核心对比：@Controller vs @RestController
 *
 * 1. 什么是 @Controller？
 *    - @Controller 是 Spring 传统的控制器注解。
 *    - 默认情况下，当方法返回一个字符串（例如 return "index";）时，Spring MVC 会将该字符串视为「视图名称 (View Name)」，
 *      并交由视图解析器（ViewResolver）去查找对应的 HTML、JSP 或 Thymeleaf 模板进行页面渲染。
 *
 * 2. 什么是 @ResponseBody？
 *    - 如果在 @Controller 的方法上添加 @ResponseBody，就会跳过视图解析器，
 *      而是通过 HttpMessageConverter 将返回值直接序列化并写入 HTTP 响应体（Response Body）中（例如纯文本、JSON）。
 *
 * 3. 为什么现代项目更常用 @RestController？
 *    - @RestController 本质是一个组合注解：
 *      @Target(ElementType.TYPE)
 *      @Retention(RetentionPolicy.RUNTIME)
 *      @Documented
 *      @Controller
 *      @ResponseBody
 *      public @interface RestController { ... }
 *    - 它省去了在每个 RESTful API 方法上重复编写 @ResponseBody 的繁琐。
 */
@Controller
@RequestMapping("/demo")
public class DemoPageController {

    /**
     * 1. 使用 @Controller + @ResponseBody 返回纯文本
     *
     * 如果去掉 @ResponseBody，Spring MVC 会尝试寻找名为 "Hello, Spring MVC Controller!" 的模板文件，
     * 从而导致 404 或 Circular view path 报错。
     */
    @GetMapping("/text")
    @ResponseBody
    public String demoText() {
        return "这是来自 @Controller 类中的方法，因为添加了 @ResponseBody 注解，内容被直接输出到了响应体中。";
    }

    /**
     * 2. 使用 @Controller + @ResponseBody 返回 JSON 对象
     *
     * 返回 Map 或 POJO 时，Spring 内部的 MappingJackson2HttpMessageConverter
     * 会自动将其序列化为 JSON 字符串，并将 Content-Type 设置为 application/json。
     */
    @GetMapping("/json")
    @ResponseBody
    public Map<String, Object> demoJson() {
        Map<String, Object> result = new HashMap<>();
        result.put("controllerType", "@Controller");
        result.put("annotationUsed", "@ResponseBody");
        result.put("description", "@RestController 实际上就是 @Controller + @ResponseBody 的合体简写！");
        return result;
    }
}
