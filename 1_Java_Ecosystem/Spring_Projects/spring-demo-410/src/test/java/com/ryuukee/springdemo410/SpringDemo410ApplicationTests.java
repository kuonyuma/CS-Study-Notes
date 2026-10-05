package com.ryuukee.springdemo410;

import com.ryuukee.springdemo410.model.User;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SpringDemo410ApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("测试创建用户 (@PostMapping, @RequestBody)")
    void testCreateUser() throws Exception {
        String userJson = """
                {
                    "username": "tester",
                    "password": "password123",
                    "nickname": "测试员",
                    "email": "tester@example.com"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("tester"));
    }

    @Test
    @DisplayName("测试根据 ID 查询用户 (@GetMapping, @PathVariable)")
    void testGetUserById() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    @DisplayName("测试文件上传 (@PostMapping, @RequestPart)")
    void testUploadAvatar() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake image content".getBytes()
        );

        mockMvc.perform(multipart("/api/users/{id}/avatar", 1)
                        .file(file)
                        .param("description", "我的自定义头像"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.originalFilename").value("avatar.png"));
    }

    @Test
    @DisplayName("测试创建任务与分页筛选 (@RequestParam, @RequestBody)")
    void testTasks() throws Exception {
        String taskJson = """
                {
                    "userId": 1,
                    "title": "单元测试任务",
                    "description": "自动执行测试",
                    "status": "PENDING"
                }
                """;

        // 创建任务
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("单元测试任务"));

        // 分页与条件筛选
        mockMvc.perform(get("/api/tasks")
                        .param("userId", "1")
                        .param("page", "1")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    @DisplayName("测试登录、Session 存储、Cookie 下发与读取 (@SessionAttribute, @CookieValue)")
    void testAuthSessionAndCookie() throws Exception {
        String loginJson = """
                {
                    "username": "admin",
                    "password": "123456"
                }
                """;

        // 1. 登录
        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Auth-Status", "SUCCESS"))
                .andExpect(cookie().exists("user_theme"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        // 2. 携带 Session 获取当前登录用户 (@SessionAttribute)
        mockMvc.perform(get("/api/auth/current-user").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("admin"));

        // 3. 携带 Cookie 获取用户偏好 (@CookieValue)
        mockMvc.perform(get("/api/auth/preference").cookie(new Cookie("user_theme", "dark")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user_theme").value("dark"));
    }

    @Test
    @DisplayName("测试客户端 Header 与 ResponseEntity 状态码设置 (@RequestHeader, ResponseEntity)")
    void testClientHeadersAndResponse() throws Exception {
        mockMvc.perform(get("/api/client/headers")
                        .header("User-Agent", "JUnit-Test-Client")
                        .header("X-Request-Id", "req-123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userAgent").value("JUnit-Test-Client"))
                .andExpect(jsonPath("$.data.customRequestId").value("req-123456"))
                .andExpect(header().exists("X-Trace-Id"));

        mockMvc.perform(get("/api/client/response-demo").param("status", "201"))
                .andExpect(status().isCreated())
                .andExpect(header().string("X-Custom-Status-Echo", "201"));
    }

    @Test
    @DisplayName("测试 @Controller 与 @ResponseBody")
    void testDemoController() throws Exception {
        mockMvc.perform(get("/demo/text"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("@ResponseBody")));

        mockMvc.perform(get("/demo/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.controllerType").value("@Controller"))
                .andExpect(jsonPath("$.annotationUsed").value("@ResponseBody"));
    }
}
