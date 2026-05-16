package com.codecollab.modules.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "任务操作日志")
public record TaskLogResponse(
    @Schema(description = "日志 ID", example = "1")
    Long id,

    @Schema(description = "操作者 ID", example = "1")
    Long operatorId,

    @Schema(description = "操作者昵称", example = "系统管理员")
    String operatorName,

    @Schema(description = "操作动作", example = "CREATE_TASK")
    String action,

    @Schema(description = "操作详情")
    String detail,

    @Schema(description = "创建时间")
    LocalDateTime createdAt
) {
}
