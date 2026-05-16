package com.codecollab.modules.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "创建任务标签请求")
public record CreateTaskLabelRequest(
    @NotBlank
    @Size(max = 64)
    @Schema(description = "标签名称", example = "前端")
    String name,

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "must be a hex color like #409EFF")
    @Schema(description = "标签颜色", example = "#409EFF")
    String color
) {
}
