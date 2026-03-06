package com.derucci.iot.common.core.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DateUtils日期工具类测试
 * TDD: 先写测试，测试失败后再实现代码
 */
@DisplayName("DateUtils日期工具类测试")
class DateUtilsTest {

    // ==================== now 测试 ====================

    @Test
    @DisplayName("now-获取当前时间")
    void should_get_current_time() {
        LocalDateTime now = DateUtils.now();
        assertThat(now).isNotNull();
    }

    @Test
    @DisplayName("nowDate-获取当前日期")
    void should_get_current_date() {
        LocalDate now = DateUtils.nowDate();
        assertThat(now).isNotNull();
    }

    // ==================== format 测试 ====================

    @Test
    @DisplayName("format-默认格式")
    void should_format_with_default_pattern() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 45);
        String result = DateUtils.format(dateTime);
        assertThat(result).isEqualTo("2024-01-15 10:30:45");
    }

    @Test
    @DisplayName("format-指定格式")
    void should_format_with_pattern() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 45);
        String result = DateUtils.format(dateTime, "yyyy-MM-dd");
        assertThat(result).isEqualTo("2024-01-15");
    }

    @Test
    @DisplayName("formatDate-日期格式化")
    void should_format_date() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        String result = DateUtils.formatDate(date);
        assertThat(result).isEqualTo("2024-01-15");
    }

    // ==================== parse 测试 ====================

    @Test
    @DisplayName("parse-默认格式解析")
    void should_parse_with_default_pattern() {
        String dateStr = "2024-01-15 10:30:45";
        LocalDateTime result = DateUtils.parse(dateStr);
        assertThat(result.getYear()).isEqualTo(2024);
        assertThat(result.getMonthValue()).isEqualTo(1);
        assertThat(result.getDayOfMonth()).isEqualTo(15);
    }

    @Test
    @DisplayName("parse-指定格式解析")
    void should_parse_with_pattern() {
        String dateStr = "2024-01-15";
        LocalDate result = DateUtils.parseDate(dateStr, "yyyy-MM-dd");
        assertThat(result.getYear()).isEqualTo(2024);
        assertThat(result.getMonthValue()).isEqualTo(1);
        assertThat(result.getDayOfMonth()).isEqualTo(15);
    }

    @Test
    @DisplayName("parseDate-日期解析")
    void should_parse_date() {
        String dateStr = "2024-01-15";
        LocalDate result = DateUtils.parseDate(dateStr);
        assertThat(result).isEqualTo(LocalDate.of(2024, 1, 15));
    }

    // ==================== plus/minus 测试 ====================

    @Test
    @DisplayName("plusDays-增加天数")
    void should_plus_days() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        LocalDate result = DateUtils.plusDays(date, 10);
        assertThat(result).isEqualTo(LocalDate.of(2024, 1, 25));
    }

    @Test
    @DisplayName("minusDays-减少天数")
    void should_minus_days() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        LocalDate result = DateUtils.minusDays(date, 5);
        assertThat(result).isEqualTo(LocalDate.of(2024, 1, 10));
    }

    @Test
    @DisplayName("plusHours-增加小时")
    void should_plus_hours() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30);
        LocalDateTime result = DateUtils.plusHours(dateTime, 5);
        assertThat(result.getHour()).isEqualTo(15);
    }

    // ==================== between 测试 ====================

    @Test
    @DisplayName("daysBetween-计算天数差")
    void should_calculate_days_between() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 15);
        long days = DateUtils.daysBetween(start, end);
        assertThat(days).isEqualTo(14);
    }

    @Test
    @DisplayName("hoursBetween-计算小时差")
    void should_calculate_hours_between() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 15, 15, 0);
        long hours = DateUtils.hoursBetween(start, end);
        assertThat(hours).isEqualTo(5);
    }

    // ==================== isAfter/isBefore 测试 ====================

    @Test
    @DisplayName("isAfter-日期之后")
    void should_return_true_when_is_after() {
        LocalDate date1 = LocalDate.of(2024, 1, 20);
        LocalDate date2 = LocalDate.of(2024, 1, 15);
        assertThat(DateUtils.isAfter(date1, date2)).isTrue();
    }

    @Test
    @DisplayName("isBefore-日期之前")
    void should_return_true_when_is_before() {
        LocalDate date1 = LocalDate.of(2024, 1, 10);
        LocalDate date2 = LocalDate.of(2024, 1, 15);
        assertThat(DateUtils.isBefore(date1, date2)).isTrue();
    }

    // ==================== startOf/endOf 测试 ====================

    @Test
    @DisplayName("startOfDay-一天开始")
    void should_get_start_of_day() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 15, 30, 45);
        LocalDateTime result = DateUtils.startOfDay(dateTime);
        assertThat(result.getHour()).isEqualTo(0);
        assertThat(result.getMinute()).isEqualTo(0);
        assertThat(result.getSecond()).isEqualTo(0);
    }

    @Test
    @DisplayName("endOfDay-一天结束")
    void should_get_end_of_day() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 45);
        LocalDateTime result = DateUtils.endOfDay(dateTime);
        assertThat(result.getHour()).isEqualTo(23);
        assertThat(result.getMinute()).isEqualTo(59);
        assertThat(result.getSecond()).isEqualTo(59);
    }

    @Test
    @DisplayName("startOfMonth-月份开始")
    void should_get_start_of_month() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        LocalDate result = DateUtils.startOfMonth(date);
        assertThat(result.getDayOfMonth()).isEqualTo(1);
    }

    @Test
    @DisplayName("endOfMonth-月份结束")
    void should_get_end_of_month() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        LocalDate result = DateUtils.endOfMonth(date);
        assertThat(result.getDayOfMonth()).isEqualTo(31);
    }

    // ==================== toTimestamp 测试 ====================

    @Test
    @DisplayName("toTimestamp-转换为时间戳")
    void should_convert_to_timestamp() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        long timestamp = DateUtils.toTimestamp(dateTime);
        assertThat(timestamp).isGreaterThan(0);
    }

    @Test
    @DisplayName("fromTimestamp-从时间戳转换")
    void should_convert_from_timestamp() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        long timestamp = DateUtils.toTimestamp(dateTime);
        LocalDateTime result = DateUtils.fromTimestamp(timestamp);
        assertThat(result.getYear()).isEqualTo(2024);
        assertThat(result.getMonthValue()).isEqualTo(1);
        assertThat(result.getDayOfMonth()).isEqualTo(15);
    }
}
