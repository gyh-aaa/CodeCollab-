package com.codecollab.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "登录响应")
public record LoginResponse(
    @Schema(description = "JWT 访问令牌")
    String accessToken,

    @Schema(description = "令牌类型", example = "Bearer")
    String tokenType,

    @Schema(description = "令牌有效期，单位秒", example = "7200")
    long expiresIn,

    @Schema(description = "当前登录用户")
    CurrentUserResponse user
) {
}
