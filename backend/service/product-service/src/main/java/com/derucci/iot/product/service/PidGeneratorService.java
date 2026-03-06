package com.derucci.iot.product.service;

import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * PID生成服务
 * 按照PRD规范：PID_ + 6位大写字母数字随机字符串
 */
@Service
public class PidGeneratorService {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int PID_SUFFIX_LENGTH = 6;
    private static final String PID_PREFIX = "PID_";
    
    private final Random random = new Random();

    /**
     * 生成唯一PID
     * 格式: PID_ + 6位大写字母数字
     * 例如: PID_A1B2C3, PID_X9Y8Z7
     */
    public String generate() {
        StringBuilder pid = new StringBuilder(PID_PREFIX);
        for (int i = 0; i < PID_SUFFIX_LENGTH; i++) {
            pid.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return pid.toString();
    }

    /**
     * 验证PID格式是否正确
     */
    public boolean isValidPidFormat(String pid) {
        if (pid == null || !pid.startsWith(PID_PREFIX)) {
            return false;
        }
        String suffix = pid.substring(PID_PREFIX.length());
        return suffix.length() == PID_SUFFIX_LENGTH && suffix.matches("[A-Z0-9]+");
    }
}
