package com.derucci.iot.common.core.exception;

/**
 * 业务异常类
 * 用于封装业务逻辑错误，包含错误码和错误消息
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final int code;

    /**
     * 私有构造函数
     */
    private BusinessException() {
        this.code = 500;
    }

    /**
     * 构造异常-带错误码和消息
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造异常-带消息(默认错误码500)
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    /**
     * 构造异常-带错误码和消息以及原因
     *
     * @param code    错误码
     * @param message 错误消息
     * @param cause   异常原因
     */
    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    // ==================== Getters ====================

    public int getCode() {
        return code;
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 参数错误 (40001)
     */
    public static BusinessException parameterError(String message) {
        return new BusinessException(40001, message);
    }

    /**
     * 资源不存在 (40401)
     */
    public static BusinessException notFound(String message) {
        return new BusinessException(40401, message);
    }

    /**
     * 未授权 (40101)
     */
    public static BusinessException unauthorized(String message) {
        return new BusinessException(40101, message);
    }

    /**
     * 禁止访问 (40301)
     */
    public static BusinessException forbidden(String message) {
        return new BusinessException(40301, message);
    }

    /**
     * 服务器内部错误 (50001)
     */
    public static BusinessException serverError(String message) {
        return new BusinessException(50001, message);
    }
}
