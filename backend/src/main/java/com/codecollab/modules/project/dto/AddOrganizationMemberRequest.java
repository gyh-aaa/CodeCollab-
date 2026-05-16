package com.codecollab.modules.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "添加组织成员请求")
public record AddOrganizationMemberRequest(
    @NotBlank
    @Schema(description = "用户名", example = "pm")
    String username,

    @NotBlank
    @Schema(description = "组织角色", example = "ADMIN")
    String memberRole
) {
}
