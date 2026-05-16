package com.codecollab.modules.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Schema(description = "更新项目请求")
public record UpdateProjectRequest(
    @NotBlank
    @Size(max = 128)
    @Schema(description = "项目名称", example = "协作平台一期")
    String name,

    @Size(max = 512)
    @Schema(description = "项目描述")
    String description,

    @Schema(description = "开始日期", example = "2026-05-16")
    LocalDate startDate,

    @Schema(description = "结束日期", example = "2026-06-16")
    LocalDate endDate
) {
}
