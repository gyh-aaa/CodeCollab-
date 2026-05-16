package com.codecollab.modules.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "任务标签")
public record TaskLabelResponse(
    @Schema(description = "标签 ID", example = "1")
    Long id,

    @Schema(description = "项目 ID", example = "1")
    Long projectId,

    @Schema(description = "标签名称", example = "前端")
    String name,

    @Schema(description = "标签颜色", example = "#409EFF")
    String color
) {
}
