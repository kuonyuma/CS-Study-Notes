package com.ryuukee.springdemo410.controller;

import com.ryuukee.springdemo410.model.ApiResponse;
import com.ryuukee.springdemo410.model.PageResult;
import com.ryuukee.springdemo410.model.Task;
import com.ryuukee.springdemo410.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务控制器
 *
 * 核心演示点：
 * 1. @RequestParam 的使用（分页、状态筛选、必填与默认值）
 * 2. @PathVariable 的使用（路径变量传递与组合）
 * 3. @RequestBody 接收 JSON 任务对象
 * 4. 常见的 HTTP Method 映射：@GetMapping, @PostMapping, @PutMapping, @DeleteMapping
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * 1. 创建新任务
     *
     * 注解解析：
     * - @PostMapping: 映射 HTTP POST /api/tasks
     * - @RequestBody: 将请求体中的 JSON 转换解析成 Task 对象
     */
    @PostMapping
    public ApiResponse<Task> createTask(@RequestBody Task task) {
        if (task.getUserId() == null) {
            return ApiResponse.error(400, "所属用户 ID (userId) 不能为空");
        }
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            return ApiResponse.error(400, "任务标题 (title) 不能为空");
        }
        Task created = taskService.createTask(task);
        return ApiResponse.success("任务创建成功", created);
    }

    /**
     * 2. 根据任务 ID 获取任务详情
     *
     * 注解解析：
     * - @GetMapping("/{id}"): 映射 HTTP GET /api/tasks/{id}
     * - @PathVariable("id"): 获取 URL 路径中的 {id} 变量
     */
    @GetMapping("/{id}")
    public ApiResponse<Task> getTaskById(@PathVariable("id") Long id) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return ApiResponse.error(404, "未找到 ID 为 " + id + " 的任务");
        }
        return ApiResponse.success(task);
    }

    /**
     * 3. 任务多条件分页与状态筛选查询
     *
     * 示例 URL: GET /api/tasks?userId=1&status=PENDING&page=1&size=5
     *
     * 注解解析：
     * - @RequestParam: 用于绑定 HTTP Request Query 参数（即 URL 中 ? 后面以 key=value 形式传递的参数）。
     *   - value: 参数名。
     *   - required: 是否必填，默认为 true。设置为 false 表示客户端不传也不会报错。
     *   - defaultValue: 默认值。若客户端未传该参数，则自动赋予默认值（同时隐式将 required 视为 false）。
     */
    @GetMapping
    public ApiResponse<PageResult<Task>> queryTasks(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {

        PageResult<Task> pageResult = taskService.queryTasks(userId, status, page, size);
        return ApiResponse.success("查询成功", pageResult);
    }

    /**
     * 4. 查询指定用户的任务列表（路径变量 + 查询参数结合）
     *
     * 示例 URL: GET /api/tasks/user/1?status=COMPLETED
     *
     * 注解解析：
     * - 同时使用 @PathVariable 获取主体定位资源（哪个用户），
     *   以及 @RequestParam 获取条件过滤（筛选什么状态）。
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<Task>> getTasksByUserId(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "status", required = false) String status) {

        List<Task> tasks = taskService.getTasksByUserId(userId, status);
        return ApiResponse.success(tasks);
    }

    /**
     * 5. 更新任务状态
     *
     * 注解解析：
     * - @PutMapping("/{id}/status"): 映射 HTTP PUT 请求，遵循 RESTful 语义中的资源更新。
     */
    @PutMapping("/{id}/status")
    public ApiResponse<String> updateTaskStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status) {

        boolean success = taskService.updateStatus(id, status);
        if (success) {
            return ApiResponse.success("任务状态已更新为: " + status, null);
        }
        return ApiResponse.error(404, "任务不存在，更新失败");
    }

    /**
     * 6. 删除任务
     *
     * 注解解析：
     * - @DeleteMapping("/{id}"): 映射 HTTP DELETE 请求，表示删除资源。
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteTask(@PathVariable("id") Long id) {
        boolean success = taskService.deleteTask(id);
        if (success) {
            return ApiResponse.success("任务已删除", null);
        }
        return ApiResponse.error(404, "任务不存在，删除失败");
    }
}
