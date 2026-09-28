package demo4;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // 1. 初始化模拟数据库 (遵循阿里巴巴规范，使用小驼峰 fakeDb)
        Map<Integer, Map<String, Object>> fakeDb = new HashMap<>();

        Map<String, Object> user1 = new HashMap<>();
        user1.put("name", "ryuke");
        user1.put("age", 20);

        Map<String, Object> user2 = new HashMap<>();
        user2.put("name", "moka");
        user2.put("age", 19);

        fakeDb.put(1, user1);
        fakeDb.put(2, user2);

        // 2. 模拟客户端场景 1：查询存在的数据，成功返回 200
        System.out.println("================ 场景 1：查询存在的用户 (ID = 1) ================");
        try {
            Response<User> response = queryUser(fakeDb, 1);
            System.out.println("【客户端收到响应】" + response);
            System.out.println("状态码: " + response.getCode());
            System.out.println("查询到的用户信息: " + response.getData());
        } catch (UserNotFoundException e) {
            System.out.println("查询发生异常: " + e.getMessage());
        }

        // 3. 模拟客户端场景 2：查询不存在的数据，抛出自定义异常
        System.out.println("\n================ 场景 2：查询不存在的用户 (ID = 3) ================");
        try {
            Response<User> response = queryUser(fakeDb, 3);
            System.out.println("【客户端收到响应】" + response);
        } catch (UserNotFoundException e) {
            // 捕获到自定义异常，模拟客户端/网关处理异常逻辑（例如转为 404 响应）
            System.out.println("【捕获到自定义异常】异常类名: " + e.getClass().getSimpleName());
            System.out.println("【异常详细信息】" + e.getMessage());

            // 模拟将异常封装为客户端错误响应 (如 404 Not Found)
            Response<User> errorResponse = Response.error(404, e.getMessage());
            System.out.println("【客户端处理后生成响应】" + errorResponse);
        }
    }

    /**
     * 模拟业务层查询用户
     *
     * @param fakeDb 模拟数据库
     * @param id     用户 ID
     * @return 状态码为 200 的成功响应对象
     * @throws UserNotFoundException 未查询到用户时抛出此自定义异常 
     */
    public static Response<User> queryUser(Map<Integer, Map<String, Object>> fakeDb, int id) {
        // 从 fakeDb 查询数据
        Map<String, Object> userData = fakeDb.get(id);

        // 场景 2：如果没查询到数据，抛出自定义异常
        if (userData == null) {
            throw new UserNotFoundException("数据库中未查询到 ID 为 " + id + " 的用户！");
        }

        // 场景 1：查询到了数据，组装 User 并返回状态码 200
        String name = (String) userData.get("name");
        int age = (int) userData.get("age");
        User user = new User(name, age);

        return Response.success(user);
    }
}
