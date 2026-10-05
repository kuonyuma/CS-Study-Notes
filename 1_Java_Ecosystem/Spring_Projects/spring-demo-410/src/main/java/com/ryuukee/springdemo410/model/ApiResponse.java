package com.ryuukee.springdemo410.model;

/**
 * 统一 API 响应结果封装类
 * 用于给客户端返回统一格式的 JSON 数据
 *
 * @param <T> 数据负载类型
 */
public class ApiResponse<T> {

    /**
     * 业务状态码 (例如: 200 成功, 400 请求参数错误, 401 未认证, 404 资源未找到)
     */
    private int code;

    /**
     * 响应提示信息
     */
    private String message;

    /**
     * 响应核心数据
     */
    private T data;

    /**
     * 响应时间戳 (毫秒)
     */
    private long timestamp;

    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "操作成功", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
