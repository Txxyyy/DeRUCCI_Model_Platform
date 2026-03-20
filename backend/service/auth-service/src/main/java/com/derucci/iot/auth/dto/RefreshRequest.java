package com.derucci.iot.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 刷新token请求DTO
 */
public class RefreshRequest {
    @NotBlank(message = "refreshToken不能为空")
    private String refreshToken;

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
