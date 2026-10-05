package com.ryuukee.springdemo410.controller;

import com.ryuukee.springdemo410.model.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 客户端请求信息与响应控制控制器
 *
 * 核心演示点：
 * 1. @RequestHeader 获取单个或全量 HTTP 请求头
 * 2. HttpServletRequest 获取客户端 IP、请求路径、HTTP Method 等环境上下文
 * 3. HttpServletResponse 设置自定义响应头与状态码
 * 4. ResponseEntity 的使用（Spring MVC 推荐的声明式控制 HTTP 响应状态码与 Header 的方式）
 */
@RestController
@RequestMapping("/api/client")
public class ClientInfoController {

    /**
     * 1. 获取客户端 HTTP Header 与网络信息
     *
     * 注解解析：
     * - @RequestHeader("User-Agent"): 获取指定的单个请求头值。
     * - @RequestHeader(value = "Accept-Language", defaultValue = "zh-CN"): 支持设置默认值与是否必填。
     * - @RequestHeader(value = "X-Request-Id", required = false): 获取自定义请求头，未提供时为 null。
     * - @RequestHeader Map<String, String> allHeaders: 一次性注入客户端发送的所有 Header 键值对。
     * - HttpServletRequest: 获取底层的请求协议、URI、请求方式、客户端 IP 地址。
     * - HttpServletResponse: 手动在响应头中加入自定义字段。
     */
    @GetMapping("/headers")
    public ApiResponse<Map<String, Object>> getClientHeaders(
            @RequestHeader(value = "User-Agent", defaultValue = "Unknown") String userAgent,
            @RequestHeader(value = "Accept-Language", defaultValue = "zh-CN") String acceptLanguage,
            @RequestHeader(value = "X-Request-Id", required = false) String customRequestId,
            @RequestHeader Map<String, String> allHeaders,
            HttpServletRequest request,
            HttpServletResponse response) {

        // 在响应头中加入服务端自定义 Header
        String traceId = (customRequestId != null) ? customRequestId : UUID.randomUUID().toString();
        response.setHeader("X-Trace-Id", traceId);
        response.setHeader("X-Server-Node", "node-spring-demo-01");

        // 收集客户端环境与 Header 信息
        Map<String, Object> data = new HashMap<>();
        data.put("userAgent", userAgent);
        data.put("acceptLanguage", acceptLanguage);
        data.put("customRequestId", customRequestId);
        data.put("clientIp", request.getRemoteAddr());
        data.put("httpMethod", request.getMethod());
        data.put("requestUri", request.getRequestURI());
        data.put("protocol", request.getProtocol());
        data.put("allHeadersCount", allHeaders.size());

        return ApiResponse.success("获取客户端信息与 Header 成功", data);
    }

    /**
     * 2. 动态设置 HTTP 状态码与响应头的演示
     *
     * 知识点解析：
     * - 除了通过 HttpServletResponse.setStatus() 设置状态码外，
     *   Spring MVC 推荐使用 ResponseEntity 作为方法返回值。
     * - ResponseEntity 允许以链式调用的方式优雅地自定义 HTTP Status Code、Headers 以及 Body 内容。
     *
     * 示例 URL: GET /api/client/response-demo?status=201
     */
    @GetMapping("/response-demo")
    public ResponseEntity<ApiResponse<String>> customResponseDemo(
            @RequestParam(value = "status", defaultValue = "200") int status) {

        HttpStatus httpStatus;
        try {
            httpStatus = HttpStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        // 构建自定义响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Custom-Status-Echo", String.valueOf(status));
        headers.add("X-Demo-Info", "Demonstrating ResponseEntity header customization");

        ApiResponse<String> body = ApiResponse.success(
                "当前 HTTP 状态码已被设置为: " + httpStatus.value() + " " + httpStatus.getReasonPhrase(),
                "Hello from ResponseEntity"
        );

        return new ResponseEntity<>(body, headers, httpStatus);
    }
}
