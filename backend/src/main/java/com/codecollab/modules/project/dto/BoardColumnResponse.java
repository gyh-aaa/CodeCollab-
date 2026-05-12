package com.codecollab.modules.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "任务看板列")
public record BoardColumnResponse(
    @Schema(description = "状态编码", example = "TODO")
    String key,

    @Schema(description = "状态名称", example = "待处理")
    String title,

    @Schema(description = "该状态下的任务卡片")
    List<TaskCardResponse> tasks
) {
}
