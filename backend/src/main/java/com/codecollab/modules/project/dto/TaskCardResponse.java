package com.codecollab.modules.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "任务卡片信息")
public record TaskCardResponse(
    @Schema(description = "任务 ID", example = "101")
    Long id,

    @Schema(description = "任务标题", example = "设计 RBAC 数据模型")
    String title,

    @Schema(description = "任务优先级", example = "HIGH")
    String priority,

    @Schema(description = "负责人名称", example = "系统管理员")
    String assigneeName,

    @Schema(description = "截止日期", example = "2026-05-18")
    String dueDate,

    @Schema(description = "任务标签", example = "[\"权限\", \"后端\"]")
    List<String> labels
) {
}
