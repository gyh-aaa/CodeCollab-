package com.codecollab.modules.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "创建任务请求")
public record CreateTaskRequest(
    @Schema(description = "父任务 ID")
    Long parentId,

    @NotBlank
    @Size(max = 160)
    @Schema(description = "任务标题", example = "实现任务 CRUD 接口")
    String title,

    @Schema(description = "任务描述")
    String description,

    @Schema(description = "任务状态", example = "TODO")
    String status,

    @Schema(description = "优先级", example = "HIGH")
    String priority,

    @Schema(description = "负责人用户 ID", example = "3")
    Long assigneeId,

    @Schema(description = "截止时间", example = "2026-05-30T18:00:00")
    LocalDateTime dueDate,

    @Schema(description = "标签 ID 列表")
    List<Long> labelIds
) {
}
