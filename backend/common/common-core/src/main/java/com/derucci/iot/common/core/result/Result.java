package com.derucci.iot.common.core.result;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * 通用响应封装类
 *
 * @param <T> 数据类型
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * 分页信息-当前页
     */
    private Integer page;

    /**
     * 分页信息-每页大小
     */
    private Integer pageSize;

    /**
     * 分页信息-总数
     */
    private Long total;

    /**
     * 私有构造函数
     */
    private Result() {
        this.timestamp = System.currentTimeMillis();
    }

    // ==================== Getters ====================

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Long getTotal() {
        return total;
    }

    // ==================== 成功响应 ====================

    /**
     * 成功响应-无数据
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "success";
        return result;
    }

    /**
     * 成功响应-带数据
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "success";
        result.data = data;
        return result;
    }

    /**
     * 成功响应-带数据和自定义消息
     */
    public static <T> Result<T> success(T data, String message) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = message;
        result.data = data;
        return result;
    }

    /**
     * 成功响应-分页数据
     */
    public static <T> Result<T> success(T data, int page, int pageSize, long total) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "success";
        result.data = data;
        result.page = page;
        result.pageSize = pageSize;
        result.total = total;
        return result;
    }

    // ==================== 错误响应 ====================

    /**
     * 错误响应-带消息
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.code = 500;
        result.message = message;
        return result;
    }

    /**
     * 错误响应-带错误码和消息
     */
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        return result;
    }

    // ==================== 业务异常响应 ====================

    /**
     * 业务异常响应
     */
    public static <T> Result<T> businessError(int code, String message) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        return result;
    }

    /**
     * 未找到响应 (404)
     */
    public static <T> Result<T> notFound(String message) {
        Result<T> result = new Result<>();
        result.code = 404;
        result.message = message;
        return result;
    }

    // ==================== 状态判断 ====================

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.code == 200;
    }
}
