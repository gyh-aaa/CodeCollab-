package com.codecollab.modules.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "任务详情")
public record TaskResponse(
    @Schema(description = "任务 ID", example = "1")
    Long id,

    @Schema(description = "项目 ID", example = "1")
    Long projectId,

    @Schema(description = "父任务 ID")
    Long parentId,

    @Schema(description = "任务标题", example = "实现任务 CRUD 接口")
    String title,

    @Schema(description = "任务描述")
    String description,

    @Schema(description = "任务状态", example = "TODO")
    String status,

    @Schema(description = "优先级", example = "HIGH")
    String priority,

    @Schema(description = "负责人 ID", example = "3")
    Long assigneeId,

    @Schema(description = "负责人昵称", example = "研发成员")
    String assigneeName,

    @Schema(description = "报告人 ID", example = "1")
    Long reporterId,

    @Schema(description = "报告人昵称", example = "系统管理员")
    String reporterName,

    @Schema(description = "截止时间")
    LocalDateTime dueDate,

    @Schema(description = "排序号", example = "10")
    Integer sortOrder,

    @Schema(description = "标签")
    List<TaskLabelResponse> labels,

    @Schema(description = "创建时间")
    LocalDateTime createdAt,

    @Schema(description = "更新时间")
    LocalDateTime updatedAt
) {
}
