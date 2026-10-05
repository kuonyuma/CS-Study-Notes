package com.ryuukee.springdemo410.service;

import com.ryuukee.springdemo410.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户业务逻辑类
 * 数据保存在内存中的 ConcurrentHashMap 中，无需依赖数据库
 */
@Service
public class UserService {

    // 内存存储：用户 ID -> 用户对象
    private final Map<Long, User> userStorage = new ConcurrentHashMap<>();

    // 自增 ID 生成器
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserService() {
        // 初始化两条演示数据，方便启动后直接测试
        initMockData();
    }

    private void initMockData() {
        long id1 = idGenerator.getAndIncrement();
        userStorage.put(id1, new User(id1, "admin", "123456", "系统管理员", "admin@example.com"));

        long id2 = idGenerator.getAndIncrement();
        userStorage.put(id2, new User(id2, "zhangsan", "123456", "张三", "zhangsan@example.com"));
    }

    /**
     * 创建新用户
     */
    public User createUser(User user) {
        long newId = idGenerator.getAndIncrement();
        user.setId(newId);
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        userStorage.put(newId, user);
        return user;
    }

    /**
     * 根据 ID 查询用户
     */
    public User getUserById(Long id) {
        return userStorage.get(id);
    }

    /**
     * 根据用户名查询用户
     */
    public User getUserByUsername(String username) {
        return userStorage.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * 查询所有用户
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.values());
    }

    /**
     * 更新用户头像文件名
     */
    public boolean updateAvatar(Long id, String avatarFileName) {
        User user = userStorage.get(id);
        if (user != null) {
            user.setAvatarFileName(avatarFileName);
            return true;
        }
        return false;
    }

    /**
     * 校验用户名与密码
     */
    public User login(String username, String password) {
        User user = getUserByUsername(username);
        if (user != null && user.getPassword() != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}
