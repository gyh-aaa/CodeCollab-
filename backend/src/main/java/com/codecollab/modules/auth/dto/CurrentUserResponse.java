package com.codecollab.modules.auth.dto;

import com.codecollab.modules.auth.AuthenticatedUser;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "当前用户信息")
public record CurrentUserResponse(
    @Schema(description = "用户 ID", example = "1")
    Long id,

    @Schema(description = "用户名", example = "admin")
    String username,

    @Schema(description = "用户昵称", example = "系统管理员")
    String nickname,

    @Schema(description = "角色编码列表", example = "[\"ADMIN\"]")
    List<String> roles,

    @Schema(description = "权限标识列表", example = "[\"*:*:*\"]")
    List<String> permissions,

    @Schema(description = "当前用户可访问菜单")
    List<MenuResponse> menus
) {
    public static CurrentUserResponse from(AuthenticatedUser user) {
        return new CurrentUserResponse(
            user.id(),
            user.username(),
            user.nickname(),
            user.roles(),
            user.permissions(),
            user.menus()
        );
    }
}
