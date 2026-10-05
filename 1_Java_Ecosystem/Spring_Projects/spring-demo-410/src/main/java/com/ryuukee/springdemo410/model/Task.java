package com.ryuukee.springdemo410.model;

import java.time.LocalDateTime;

/**
 * 任务实体类
 * 保存在内存中的用户任务
 */
public class Task {

    private Long id;
    private Long userId;
    private String title;
    private String description;
    private String status; // 例如: PENDING (待处理), IN_PROGRESS (进行中), COMPLETED (已完成)
    private LocalDateTime createdAt;

    public Task() {
    }

    public Task(Long id, Long userId, String title, String description, String status) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
