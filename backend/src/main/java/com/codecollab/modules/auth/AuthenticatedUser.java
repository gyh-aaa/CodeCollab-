package com.codecollab.modules.auth;

import com.codecollab.modules.auth.dto.MenuResponse;
import java.util.List;

public record AuthenticatedUser(
    Long id,
    String username,
    String nickname,
    List<String> roles,
    List<String> permissions,
    List<MenuResponse> menus
) {
}
