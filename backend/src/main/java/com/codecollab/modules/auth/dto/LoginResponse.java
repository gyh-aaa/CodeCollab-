package com.codecollab.modules.auth.dto;

public record LoginResponse(
    String accessToken,
    String tokenType,
    long expiresIn,
    CurrentUserResponse user
) {
}
