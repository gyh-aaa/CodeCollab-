package com.codecollab.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "菜单信息")
public record MenuResponse(
    @Schema(description = "菜单编码", example = "dashboard")
    String key,

    @Schema(description = "菜单标题", example = "工作台")
    String title,

    @Schema(description = "前端路由路径", example = "/dashboard")
    String path,

    @Schema(description = "图标名称", example = "House")
    String icon,

    @Schema(description = "访问该菜单需要的权限标识", example = "dashboard:view")
    String permission
) {
}
