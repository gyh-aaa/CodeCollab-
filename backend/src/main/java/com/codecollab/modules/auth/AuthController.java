package com.codecollab.modules.auth;

import com.codecollab.common.ApiResponse;
import com.codecollab.modules.auth.dto.CurrentUserResponse;
import com.codecollab.modules.auth.dto.LoginRequest;
import com.codecollab.modules.auth.dto.LoginResponse;
import com.codecollab.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录、当前用户信息和 Token 相关接口")
public class AuthController {

    private final UserAccountService userAccountService;
    private final JwtService jwtService;

    public AuthController(UserAccountService userAccountService, JwtService jwtService) {
        this.userAccountService = userAccountService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名和密码登录，成功后返回 JWT 访问令牌和当前用户信息。")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthenticatedUser user = userAccountService.authenticate(request.username(), request.password());
        String token = jwtService.generateToken(
            user.username(),
            Map.of(
                "uid", user.id(),
                "nickname", user.nickname(),
                "roles", user.roles(),
                "permissions", user.permissions()
            )
        );

        CurrentUserResponse currentUser = CurrentUserResponse.from(user);
        return ApiResponse.ok(new LoginResponse(token, "Bearer", jwtService.expiresInSeconds(), currentUser));
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前登录用户", description = "根据请求头中的 Bearer Token 获取当前用户信息。")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<CurrentUserResponse> me(Authentication authentication) {
        AuthenticatedUser user = userAccountService.findByUsername(authentication.getName());
        return ApiResponse.ok(CurrentUserResponse.from(user));
    }
}
