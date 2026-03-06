package com.derucci.iot.common.core.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * BusinessException业务异常类测试
 * TDD: 先写测试，测试失败后再实现代码
 */
@DisplayName("BusinessException业务异常类测试")
class BusinessExceptionTest {

    // ==================== 构造函数测试 ====================

    @Test
    @DisplayName("构造异常-带错误码和消息")
    void should_create_exception_with_code_and_message() {
        // Given
        int errorCode = 40001;
        String message = "参数错误";

        // When
        BusinessException exception = new BusinessException(errorCode, message);

        // Then
        assertThat(exception.getCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("构造异常-带消息(默认错误码)")
    void should_create_exception_with_message_only() {
        // Given
        String message = "操作失败";

        // When
        BusinessException exception = new BusinessException(message);

        // Then
        assertThat(exception.getCode()).isEqualTo(500);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("构造异常-带错误码和消息以及原因")
    void should_create_exception_with_cause() {
        // Given
        int errorCode = 40401;
        String message = "资源不存在";
        Throwable cause = new RuntimeException("原始异常");

        // When
        BusinessException exception = new BusinessException(errorCode, message, cause);

        // Then
        assertThat(exception.getCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    // ==================== 静态工厂方法测试 ====================

    @Test
    @DisplayName("静态工厂方法-参数错误")
    void should_return_parameter_error() {
        // Given
        String message = "参数不能为空";

        // When
        BusinessException exception = BusinessException.parameterError(message);

        // Then
        assertThat(exception.getCode()).isEqualTo(40001);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("静态工厂方法-资源不存在")
    void should_return_not_found_error() {
        // Given
        String message = "用户不存在";

        // When
        BusinessException exception = BusinessException.notFound(message);

        // Then
        assertThat(exception.getCode()).isEqualTo(40401);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("静态工厂方法-未授权")
    void should_return_unauthorized_error() {
        // Given
        String message = "Token已过期";

        // When
        BusinessException exception = BusinessException.unauthorized(message);

        // Then
        assertThat(exception.getCode()).isEqualTo(40101);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("静态工厂方法-禁止访问")
    void should_return_forbidden_error() {
        // Given
        String message = "没有权限";

        // When
        BusinessException exception = BusinessException.forbidden(message);

        // Then
        assertThat(exception.getCode()).isEqualTo(40301);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("静态工厂方法-服务器内部错误")
    void should_return_server_error() {
        // Given
        String message = "数据库连接失败";

        // When
        BusinessException exception = BusinessException.serverError(message);

        // Then
        assertThat(exception.getCode()).isEqualTo(50001);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    // ==================== 异常抛出测试 ====================

    @Test
    @DisplayName("抛出业务异常")
    void should_throw_business_exception() {
        // Given
        int code = 40001;
        String message = "业务异常";

        // When & Then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> { throw new BusinessException(code, message); }
        );

        assertThat(exception.getCode()).isEqualTo(code);
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
