package com.codecollab.modules.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "创建组织请求")
public record CreateOrganizationRequest(
    @NotBlank
    @Size(max = 128)
    @Schema(description = "组织名称", example = "CodeCollab 产品组")
    String name,

    @Size(max = 512)
    @Schema(description = "组织描述", example = "负责项目协作平台的研发与交付")
    String description
) {
}
