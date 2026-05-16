package com.codecollab.modules.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "任务状态流转请求")
public record ChangeTaskStatusRequest(
    @NotBlank
    @Schema(description = "目标状态", example = "IN_PROGRESS")
    String status
) {
}
