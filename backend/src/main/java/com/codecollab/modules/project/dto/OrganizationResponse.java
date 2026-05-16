package com.codecollab.modules.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "组织信息")
public record OrganizationResponse(
    @Schema(description = "组织 ID", example = "1")
    Long id,

    @Schema(description = "组织名称", example = "CodeCollab 产品组")
    String name,

    @Schema(description = "组织描述")
    String description,

    @Schema(description = "负责人用户 ID", example = "1")
    Long ownerId,

    @Schema(description = "负责人昵称", example = "系统管理员")
    String ownerName,

    @Schema(description = "组织状态", example = "ACTIVE")
    String status,

    @Schema(description = "当前用户在组织中的角色", example = "OWNER")
    String currentUserRole,

    @Schema(description = "成员数", example = "3")
    long memberCount,

    @Schema(description = "项目数", example = "2")
    long projectCount,

    @Schema(description = "创建时间")
    LocalDateTime createdAt
) {
}
