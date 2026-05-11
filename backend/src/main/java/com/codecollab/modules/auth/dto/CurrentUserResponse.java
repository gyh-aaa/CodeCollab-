package com.codecollab.modules.auth.dto;

import java.util.List;

public record CurrentUserResponse(
    Long id,
    String username,
    String nickname,
    List<String> roles,
    List<String> permissions
) {
}
