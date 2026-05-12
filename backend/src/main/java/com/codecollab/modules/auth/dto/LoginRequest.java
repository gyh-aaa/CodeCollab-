package com.codecollab.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "登录请求参数")
public record LoginRequest(
    @Schema(description = "用户名", example = "admin")
    @NotBlank String username,

    @Schema(description = "密码", example = "Admin@123456")
    @NotBlank String password
) {
}
