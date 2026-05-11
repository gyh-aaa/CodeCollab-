package com.codecollab.modules.auth;

import com.codecollab.common.ApiResponse;
import com.codecollab.modules.auth.dto.CurrentUserResponse;
import com.codecollab.modules.auth.dto.LoginRequest;
import com.codecollab.modules.auth.dto.LoginResponse;
import com.codecollab.security.JwtService;
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
public class AuthController {

    private final DemoUserService demoUserService;
    private final JwtService jwtService;

    public AuthController(DemoUserService demoUserService, JwtService jwtService) {
        this.demoUserService = demoUserService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        DemoUserService.DemoUser user = demoUserService.authenticate(request.username(), request.password());
        String token = jwtService.generateToken(
            user.username(),
            Map.of(
                "uid", user.id(),
                "nickname", user.nickname(),
                "roles", user.roles(),
                "permissions", user.permissions()
            )
        );

        CurrentUserResponse currentUser = new CurrentUserResponse(
            user.id(),
            user.username(),
            user.nickname(),
            user.roles(),
            user.permissions()
        );
        return ApiResponse.ok(new LoginResponse(token, "Bearer", jwtService.expiresInSeconds(), currentUser));
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserResponse> me(Authentication authentication) {
        DemoUserService.DemoUser user = demoUserService.findByUsername(authentication.getName());
        return ApiResponse.ok(new CurrentUserResponse(
            user.id(),
            user.username(),
            user.nickname(),
            user.roles(),
            user.permissions()
        ));
    }
}
