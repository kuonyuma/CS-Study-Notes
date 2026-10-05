package com.ryuukee.springdemo410.controller;

import com.ryuukee.springdemo410.model.ApiResponse;
import com.ryuukee.springdemo410.model.User;
import com.ryuukee.springdemo410.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 用户控制器
 *
 * 注解解析：
 * - @RestController: 组合注解，等同于 @Controller + @ResponseBody。
 *   表示该类是 Spring MVC 的控制器组件，且类中所有方法默认直接将返回值序列化（通常是 JSON）写入 HTTP 响应体，
 *   而不是被视图解析器解析为 HTML/JSP 页面路径。
 *
 * - @RequestMapping("/api/users"): 映射请求路径前缀。
 *   表示该控制器中所有方法的基础 URL 路径都是以 /api/users 开头。
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 1. 创建新用户
     *
     * 注解解析：
     * - @PostMapping: 缩写注解，相当于 @RequestMapping(method = RequestMethod.POST)。
     *   用于处理 HTTP POST 请求，通常用于创建资源。
     * - @RequestBody: 将客户端发送的 HTTP 请求体中的 JSON 数据反序列化为 Java 对象 (User)。
     *   Spring 内部使用 HttpMessageConverter (基于 Jackson 库) 自动完成转换。
     */
    @PostMapping
    public ApiResponse<User> createUser(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return ApiResponse.error(400, "用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return ApiResponse.error(400, "密码不能为空");
        }
        User createdUser = userService.createUser(user);
        return ApiResponse.success("用户创建成功", createdUser);
    }

    /**
     * 2. 根据 ID 查询用户
     *
     * 注解解析：
     * - @GetMapping("/{id}"): 处理 HTTP GET 请求，URL 模式为 /api/users/{id}。
     * - @PathVariable("id"): 路径变量注解。
     *   用于从请求 URI 模板中提取动态占位符 {id} 的值，并自动转换为方法参数指定的类型 (Long)。
     */
    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ApiResponse.error(404, "未找到 ID 为 " + id + " 的用户");
        }
        return ApiResponse.success(user);
    }

    /**
     * 3. 查询所有用户列表
     *
     * 注解解析：
     * - @GetMapping: 匹配没有子路径的 GET /api/users。
     */
    @GetMapping
    public ApiResponse<List<User>> getAllUsers() {
        List<User> list = userService.getAllUsers();
        return ApiResponse.success(list);
    }

    /**
     * 4. 上传用户头像
     *
     * 注解解析：
     * - consumes = MediaType.MULTIPART_FORM_DATA_VALUE: 指定该接口仅接收 multipart/form-data 类型的表单请求。
     * - @RequestPart("file"): 用于接收 multipart 请求中的具体某一部分文件 (Part)。
     *   与 @RequestParam 的区别：
     *   1) @RequestParam 既能接收简单表单文本字段，也能接收上传的文件，但本质是作为 query/form 参数对待；
     *   2) @RequestPart 则专门配合 multipart/form-data 使用，能够基于每部分的 Content-Type 触发复杂的 HttpMessageConverter 转换（例如上传文件的同时传递复杂 JSON 对象）。
     */
    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Map<String, Object>> uploadAvatar(
            @PathVariable("id") Long id,
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {

        // 检查用户是否存在
        User user = userService.getUserById(id);
        if (user == null) {
            return ApiResponse.error(404, "用户不存在，无法上传头像");
        }

        // 检查文件是否为空
        if (file.isEmpty()) {
            return ApiResponse.error(400, "上传文件不能为空");
        }

        try {
            // 获取原始文件名与扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 生成唯一文件名，防止重名覆盖
            String uniqueFileName = UUID.randomUUID() + extension;

            // 存放在当前工程下的 uploads 目录
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path targetPath = uploadDir.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 更新用户头像信息
            userService.updateAvatar(id, uniqueFileName);

            Map<String, Object> result = new HashMap<>();
            result.put("userId", id);
            result.put("originalFilename", originalFilename);
            result.put("savedFileName", uniqueFileName);
            result.put("fileSize", file.getSize());
            result.put("contentType", file.getContentType());
            result.put("description", description != null ? description : "无描述");

            return ApiResponse.success("头像上传成功", result);
        } catch (IOException e) {
            return ApiResponse.error(500, "文件保存失败: " + e.getMessage());
        }
    }
}
