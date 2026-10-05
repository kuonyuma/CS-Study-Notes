package com.ryuukee.springdemo410.service;

import com.ryuukee.springdemo410.model.PageResult;
import com.ryuukee.springdemo410.model.Task;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 任务业务逻辑类
 * 数据保存在内存中的 ConcurrentHashMap 中
 */
@Service
public class TaskService {

    // 内存存储：任务 ID -> 任务对象
    private final Map<Long, Task> taskStorage = new ConcurrentHashMap<>();

    // 任务自增 ID 生成器
    private final AtomicLong idGenerator = new AtomicLong(1);

    public TaskService() {
        initMockData();
    }

    private void initMockData() {
        long t1 = idGenerator.getAndIncrement();
        taskStorage.put(t1, new Task(t1, 1L, "学习 @Controller 与 @RestController 的区别", "对比两者的返回值处理与视图解析", "COMPLETED"));

        long t2 = idGenerator.getAndIncrement();
        taskStorage.put(t2, new Task(t2, 1L, "掌握 @RequestParam 与 @PathVariable 的传参", "理解 Query 参数与路径变量的使用场景", "IN_PROGRESS"));

        long t3 = idGenerator.getAndIncrement();
        taskStorage.put(t3, new Task(t3, 1L, "理解 @RequestBody 与 JSON 序列化", "测试客户端发送 JSON 与服务端自动反序列化", "PENDING"));

        long t4 = idGenerator.getAndIncrement();
        taskStorage.put(t4, new Task(t4, 1L, "实现 @RequestPart 头像文件上传", "处理 multipart/form-data 格式表单", "PENDING"));

        long t5 = idGenerator.getAndIncrement();
        taskStorage.put(t5, new Task(t5, 1L, "测试 Session 与 Cookie 的状态管理", "使用 @SessionAttribute 与 @CookieValue", "PENDING"));

        long t6 = idGenerator.getAndIncrement();
        taskStorage.put(t6, new Task(t6, 2L, "张三的 Spring MVC 预习任务", "快速熟悉 Spring 常用注解", "COMPLETED"));
    }

    /**
     * 创建任务
     */
    public Task createTask(Task task) {
        long newId = idGenerator.getAndIncrement();
        task.setId(newId);
        if (task.getCreatedAt() == null) {
            task.setCreatedAt(LocalDateTime.now());
        }
        if (task.getStatus() == null || task.getStatus().trim().isEmpty()) {
            task.setStatus("PENDING");
        }
        taskStorage.put(newId, task);
        return task;
    }

    /**
     * 根据 ID 查询任务
     */
    public Task getTaskById(Long id) {
        return taskStorage.get(id);
    }

    /**
     * 根据用户 ID 和状态查询任务列表
     */
    public List<Task> getTasksByUserId(Long userId, String status) {
        return taskStorage.values().stream()
                .filter(t -> t.getUserId() != null && t.getUserId().equals(userId))
                .filter(t -> status == null || status.trim().isEmpty() || t.getStatus().equalsIgnoreCase(status.trim()))
                .collect(Collectors.toList());
    }

    /**
     * 多条件分页查询任务
     *
     * @param userId 可选用户 ID
     * @param status 可选状态
     * @param page   页码 (从 1 开始)
     * @param size   每页大小
     * @return 分页结果对象
     */
    public PageResult<Task> queryTasks(Long userId, String status, int page, int size) {
        // 1. 根据条件过滤
        List<Task> filtered = taskStorage.values().stream()
                .filter(t -> userId == null || (t.getUserId() != null && t.getUserId().equals(userId)))
                .filter(t -> status == null || status.trim().isEmpty() || t.getStatus().equalsIgnoreCase(status.trim()))
                .sorted((a, b) -> Long.compare(b.getId(), a.getId())) // 按 ID 降序排列
                .collect(Collectors.toList());

        int total = filtered.size();
        if (page < 1) {
            page = 1;
        }
        if (size < 1) {
            size = 5;
        }

        // 2. 内存分页计算
        int fromIndex = (page - 1) * size;
        if (fromIndex >= total) {
            return new PageResult<>(new ArrayList<>(), total, page, size);
        }

        int toIndex = Math.min(fromIndex + size, total);
        List<Task> pagedList = filtered.subList(fromIndex, toIndex);

        return new PageResult<>(pagedList, total, page, size);
    }

    /**
     * 更新任务状态
     */
    public boolean updateStatus(Long id, String status) {
        Task task = taskStorage.get(id);
        if (task != null) {
            task.setStatus(status);
            return true;
        }
        return false;
    }

    /**
     * 删除任务
     */
    public boolean deleteTask(Long id) {
        return taskStorage.remove(id) != null;
    }
}
