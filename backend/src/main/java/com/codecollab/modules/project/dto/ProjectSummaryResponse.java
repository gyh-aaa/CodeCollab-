package com.codecollab.modules.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "项目摘要信息")
public record ProjectSummaryResponse(
    @Schema(description = "项目 ID", example = "1")
    Long id,

    @Schema(description = "项目名称", example = "协作平台一期")
    String name,

    @Schema(description = "项目编码", example = "CCP")
    String code,

    @Schema(description = "项目状态", example = "ACTIVE")
    String status,

    @Schema(description = "任务总数", example = "18")
    int totalTasks,

    @Schema(description = "已完成任务数", example = "7")
    int completedTasks,

    @Schema(description = "负责人名称", example = "系统管理员")
    String ownerName
) {
}
