package com.codecollab.modules.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Schema(description = "创建项目请求")
public record CreateProjectRequest(
    @NotNull
    @Schema(description = "组织 ID", example = "1")
    Long organizationId,

    @NotBlank
    @Size(max = 128)
    @Schema(description = "项目名称", example = "协作平台三期")
    String name,

    @NotBlank
    @Pattern(regexp = "^[A-Z0-9_]{2,32}$", message = "must contain 2-32 uppercase letters, digits or underscores")
    @Schema(description = "项目编码", example = "CCP3")
    String code,

    @Size(max = 512)
    @Schema(description = "项目描述", example = "组织与项目管理能力")
    String description,

    @Schema(description = "开始日期", example = "2026-05-16")
    LocalDate startDate,

    @Schema(description = "结束日期", example = "2026-06-16")
    LocalDate endDate
) {
}
