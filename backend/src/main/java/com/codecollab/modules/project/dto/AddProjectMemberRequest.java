package com.codecollab.modules.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "添加项目成员请求")
public record AddProjectMemberRequest(
    @NotBlank
    @Schema(description = "用户名", example = "member")
    String username,

    @NotBlank
    @Schema(description = "项目角色", example = "DEVELOPER")
    String projectRole
) {
}
