package com.codecollab.modules.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "项目详情")
public record ProjectDetailResponse(
    @Schema(description = "项目 ID", example = "1")
    Long id,

    @Schema(description = "组织 ID", example = "1")
    Long organizationId,

    @Schema(description = "组织名称", example = "CodeCollab 产品组")
    String organizationName,

    @Schema(description = "项目名称", example = "协作平台一期")
    String name,

    @Schema(description = "项目编码", example = "CCP")
    String code,

    @Schema(description = "项目描述")
    String description,

    @Schema(description = "负责人用户 ID", example = "1")
    Long ownerId,

    @Schema(description = "负责人昵称", example = "系统管理员")
    String ownerName,

    @Schema(description = "项目状态", example = "ACTIVE")
    String status,

    @Schema(description = "开始日期", example = "2026-05-16")
    LocalDate startDate,

    @Schema(description = "结束日期", example = "2026-06-16")
    LocalDate endDate,

    @Schema(description = "当前用户在项目中的角色", example = "OWNER")
    String currentUserRole,

    @Schema(description = "任务总数", example = "18")
    int totalTasks,

    @Schema(description = "已完成任务数", example = "7")
    int completedTasks,

    @Schema(description = "成员数", example = "3")
    long memberCount
) {
}
