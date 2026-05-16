package com.codecollab.modules.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "更新组织请求")
public record UpdateOrganizationRequest(
    @NotBlank
    @Size(max = 128)
    @Schema(description = "组织名称", example = "CodeCollab 产品组")
    String name,

    @Size(max = 512)
    @Schema(description = "组织描述")
    String description
) {
}
