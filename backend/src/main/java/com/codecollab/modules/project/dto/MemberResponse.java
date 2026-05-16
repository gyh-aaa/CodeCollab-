package com.codecollab.modules.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "成员信息")
public record MemberResponse(
    @Schema(description = "成员关系 ID", example = "1")
    Long id,

    @Schema(description = "用户 ID", example = "1")
    Long userId,

    @Schema(description = "用户名", example = "admin")
    String username,

    @Schema(description = "昵称", example = "系统管理员")
    String nickname,

    @Schema(description = "邮箱", example = "admin@codecollab.local")
    String email,

    @Schema(description = "成员角色", example = "OWNER")
    String role,

    @Schema(description = "加入时间")
    LocalDateTime joinedAt
) {
}
