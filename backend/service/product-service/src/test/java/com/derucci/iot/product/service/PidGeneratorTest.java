package com.derucci.iot.product.service;

import com.derucci.iot.product.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * PID生成器测试
 * 按照TDD流程：先写测试 -> 运行失败 -> 编写实现 -> 测试通过
 */
class PidGeneratorTest {

    @Test
    @DisplayName("PID格式应为 PID_ + 6位大写字母数字")
    void testPidFormat() {
        // Given: 需要生成PID
        // When: 调用PID生成方法
        String pid = generatePid();
        
        // Then: 验证格式正确
        assertNotNull(pid);
        assertTrue(pid.startsWith("PID_"), "PID应该以PID_开头");
        assertEquals(9, pid.length(), "PID长度应为9位");
        
        // 验证后6位是大写字母或数字
        String suffix = pid.substring(4);
        assertTrue(suffix.matches("[A-Z0-9]{6}"), "后6位应该是大写字母或数字");
    }

    @Test
    @DisplayName("多次生成的PID应该唯一")
    void testPidUniqueness() {
        // Given: 生成多个PID
        // When: 
        String pid1 = generatePid();
        String pid2 = generatePid();
        String pid3 = generatePid();
        
        // Then: 验证唯一性
        assertNotEquals(pid1, pid2, "PID1和PID2应该不同");
        assertNotEquals(pid2, pid3, "PID2和PID3应该不同");
        assertNotEquals(pid1, pid3, "PID1和PID3应该不同");
    }

    @Test
    @DisplayName("Product实体应该能设置PID")
    void testProductWithPid() {
        // Given: 创建产品实体
        Product product = new Product();
        
        // When: 设置PID
        String pid = generatePid();
        product.setPid(pid);
        
        // Then: 验证PID设置成功
        assertNotNull(product.getPid());
        assertEquals(pid, product.getPid());
    }

    // 临时实现，用于测试编译
    private String generatePid() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder pid = new StringBuilder("PID_");
        for (int i = 0; i < 6; i++) {
            pid.append(chars.charAt((int)(Math.random() * chars.length())));
        }
        return pid.toString();
    }
}
