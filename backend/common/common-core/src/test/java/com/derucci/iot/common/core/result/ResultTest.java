package com.derucci.iot.common.core.result;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Result通用响应类测试
 * TDD: 先写测试，测试失败后再实现代码
 */
@DisplayName("Result通用响应类测试")
class ResultTest {

    // ==================== 成功响应测试 ====================

    @Test
    @DisplayName("成功响应-带数据")
    void should_return_success_with_data() {
        // Given
        String data = "test data";

        // When
        Result<String> result = Result.success(data);

        // Then
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("success");
        assertThat(result.getData()).isEqualTo(data);
    }

    @Test
    @DisplayName("成功响应-不带数据")
    void should_return_success_without_data() {
        // When
        Result<Void> result = Result.success();

        // Then
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("success");
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("成功响应-带自定义消息")
    void should_return_success_with_custom_message() {
        // Given
        String message = "操作成功";
        Integer data = 100;

        // When
        Result<Integer> result = Result.success(data, message);

        // Then
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(result.getData()).isEqualTo(100);
    }

    // ==================== 错误响应测试 ====================

    @Test
    @DisplayName("错误响应-带错误消息")
    void should_return_error_with_message() {
        // Given
        String message = "操作失败";

        // When
        Result<Void> result = Result.error(message);

        // Then
        assertThat(result.getCode()).isEqualTo(500);
        assertThat(result.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("错误响应-带错误码和消息")
    void should_return_error_with_code_and_message() {
        // Given
        int code = 40001;
        String message = "参数错误";

        // When
        Result<Void> result = Result.error(code, message);

        // Then
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getMessage()).isEqualTo(message);
    }

    // ==================== 业务异常响应测试 ====================

    @Test
    @DisplayName("业务异常响应")
    void should_return_business_error() {
        // Given
        int code = 40401;
        String message = "资源不存在";

        // When
        Result<Void> result = Result.businessError(code, message);

        // Then
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getMessage()).isEqualTo(message);
    }

    // ==================== 分页响应测试 ====================

    @Test
    @DisplayName("分页响应")
    void should_return_paged_result() {
        // Given
        var data = java.util.List.of("item1", "item2", "item3");
        int page = 1;
        int pageSize = 10;
        long total = 100;

        // When
        Result<java.util.List<String>> result = Result.success(data, page, pageSize, total);

        // Then
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData()).hasSize(3);
    }

    // ==================== 状态判断测试 ====================

    @Test
    @DisplayName("判断是否成功-成功")
    void should_return_true_when_success() {
        // Given
        Result<String> result = Result.success("data");

        // Then
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("判断是否成功-失败")
    void should_return_false_when_error() {
        // Given
        Result<Void> result = Result.error("error");

        // Then
        assertThat(result.isSuccess()).isFalse();
    }

    // ==================== 时间戳测试 ====================

    @Test
    @DisplayName("时间戳应该存在")
    void should_have_timestamp() {
        // Given
        long before = System.currentTimeMillis();

        // When
        Result<String> result = Result.success("data");

        // Then
        long after = System.currentTimeMillis();
        assertThat(result.getTimestamp()).isGreaterThanOrEqualTo(before);
        assertThat(result.getTimestamp()).isLessThanOrEqualTo(after);
    }
}
