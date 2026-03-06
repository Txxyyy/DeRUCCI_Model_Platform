package com.derucci.iot.common.core.handler;

import com.derucci.iot.common.core.exception.BusinessException;
import com.derucci.iot.common.core.result.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GlobalExceptionHandler全局异常处理器测试
 * TDD: 先写测试，测试失败后再实现代码
 */
@DisplayName("GlobalExceptionHandler全局异常处理器测试")
class GlobalExceptionHandlerTest {

    // 创建一个测试用的GlobalExceptionHandler
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // ==================== BusinessException 测试 ====================

    @Test
    @DisplayName("处理BusinessException-业务错误")
    void should_handle_business_exception() {
        // Given
        BusinessException exception = new BusinessException(40001, "业务错误");

        // When
        Result<?> result = handler.handleBusinessException(exception);

        // Then
        assertThat(result.getCode()).isEqualTo(40001);
        assertThat(result.getMessage()).isEqualTo("业务错误");
    }

    @Test
    @DisplayName("处理BusinessException-默认错误码")
    void should_handle_business_exception_with_default_code() {
        // Given
        BusinessException exception = new BusinessException("服务器错误");

        // When
        Result<?> result = handler.handleBusinessException(exception);

        // Then
        assertThat(result.getCode()).isEqualTo(500);
        assertThat(result.getMessage()).isEqualTo("服务器错误");
    }

    @Test
    @DisplayName("处理BusinessException-资源不存在")
    void should_handle_not_found_exception() {
        // Given
        BusinessException exception = BusinessException.notFound("用户不存在");

        // When
        Result<?> result = handler.handleBusinessException(exception);

        // Then
        assertThat(result.getCode()).isEqualTo(40401);
        assertThat(result.getMessage()).isEqualTo("用户不存在");
    }

    @Test
    @DisplayName("处理BusinessException-参数错误")
    void should_handle_parameter_exception() {
        // Given
        BusinessException exception = BusinessException.parameterError("参数不能为空");

        // When
        Result<?> result = handler.handleBusinessException(exception);

        // Then
        assertThat(result.getCode()).isEqualTo(40001);
    }

    @Test
    @DisplayName("处理BusinessException-未授权")
    void should_handle_unauthorized_exception() {
        // Given
        BusinessException exception = BusinessException.unauthorized("Token已过期");

        // When
        Result<?> result = handler.handleBusinessException(exception);

        // Then
        assertThat(result.getCode()).isEqualTo(40101);
    }

    @Test
    @DisplayName("处理BusinessException-禁止访问")
    void should_handle_forbidden_exception() {
        // Given
        BusinessException exception = BusinessException.forbidden("没有权限");

        // When
        Result<?> result = handler.handleBusinessException(exception);

        // Then
        assertThat(result.getCode()).isEqualTo(40301);
    }

    @Test
    @DisplayName("处理BusinessException-服务器错误")
    void should_handle_server_exception() {
        // Given
        BusinessException exception = BusinessException.serverError("数据库连接失败");

        // When
        Result<?> result = handler.handleBusinessException(exception);

        // Then
        assertThat(result.getCode()).isEqualTo(50001);
    }

    // ==================== IllegalArgumentException 测试 ====================

    @Test
    @DisplayName("处理IllegalArgumentException")
    void should_handle_illegal_argument_exception() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("参数不合法");

        // When
        Result<?> result = handler.handleIllegalArgumentException(exception);

        // Then
        assertThat(result.getCode()).isEqualTo(40001);
        assertThat(result.getMessage()).isEqualTo("参数不合法");
    }

    // ==================== 其他异常测试 ====================

    @Test
    @DisplayName("处理未知异常-默认消息")
    void should_handle_unknown_exception() {
        // Given
        RuntimeException exception = new RuntimeException("未知错误");

        // When
        Result<?> result = handler.handleException(exception);

        // Then
        assertThat(result.getCode()).isEqualTo(500);
        assertThat(result.getMessage()).isEqualTo("系统内部错误");
    }

    @Test
    @DisplayName("处理空指针异常")
    void should_handle_null_pointer_exception() {
        // Given
        NullPointerException exception = new NullPointerException("空指针");

        // When
        Result<?> result = handler.handleException(exception);

        // Then
        assertThat(result.getCode()).isEqualTo(50001);
    }

    @Test
    @DisplayName("处理未知异常-带原因消息")
    void should_handle_exception_with_cause_message() {
        // Given
        RuntimeException exception = new RuntimeException("原始错误");

        // When
        Result<?> result = handler.handleException(exception);

        // Then
        assertThat(result.getCode()).isEqualTo(500);
    }
}
