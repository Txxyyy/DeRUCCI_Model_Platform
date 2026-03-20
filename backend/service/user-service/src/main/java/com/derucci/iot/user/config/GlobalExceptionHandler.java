package com.derucci.iot.user.config;

import com.derucci.iot.common.core.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 全局异常处理器，统一捕获并转换异常为标准响应格式 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理权限不足异常，返回403
     *
     * @param e 安全异常
     * @return 包含错误信息的403响应
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Result<Void>> handleSecurityException(SecurityException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Result.error(403, e.getMessage()));
    }

    /**
     * 处理参数非法异常，返回400
     *
     * @param e 参数非法异常
     * @return 包含错误信息的400响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.error(400, e.getMessage()));
    }

    /**
     * 处理未捕获的通用异常，返回500
     *
     * @param e 未捕获的异常
     * @return 包含错误信息的500响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(500, "服务器内部错误: " + e.getMessage()));
    }
}
