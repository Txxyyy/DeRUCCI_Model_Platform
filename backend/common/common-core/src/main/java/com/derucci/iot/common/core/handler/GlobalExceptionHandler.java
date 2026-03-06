package com.derucci.iot.common.core.handler;

import com.derucci.iot.common.core.exception.BusinessException;
import com.derucci.iot.common.core.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一处理Spring Boot应用中的各种异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理BusinessException业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        logger.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.businessError(e.getCode(), e.getMessage());
    }

    /**
     * 处理IllegalArgumentException参数不合法异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("参数不合法: {}", e.getMessage());
        return Result.businessError(40001, e.getMessage());
    }

    /**
     * 处理NullPointerException空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<?> handleNullPointerException(NullPointerException e) {
        logger.error("空指针异常", e);
        return Result.businessError(50001, "系统内部错误");
    }

    /**
     * 处理未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        // 先检查是否是NullPointerException
        if (e instanceof NullPointerException) {
            return handleNullPointerException((NullPointerException) e);
        }
        logger.error("系统内部错误", e);
        return Result.businessError(500, "系统内部错误");
    }
}
