package com.codecollab.modules.auth;

import com.codecollab.common.BusinessException;
import com.codecollab.common.ErrorCode;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DemoUserService {

    private static final String DEMO_PASSWORD = "Admin@123456";

    private final Map<String, DemoUser> users = Map.of(
        "admin", new DemoUser(1L, "admin", "系统管理员", List.of("ADMIN"), List.of("*:*:*")),
        "pm", new DemoUser(2L, "pm", "项目负责人", List.of("PROJECT_MANAGER"), List.of("project:read", "task:write")),
        "member", new DemoUser(3L, "member", "研发成员", List.of("MEMBER"), List.of("project:read", "task:read"))
    );

    public DemoUser authenticate(String username, String password) {
        DemoUser user = users.get(username);
        if (user == null || !DEMO_PASSWORD.equals(password)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "账号或密码错误");
        }
        return user;
    }

    public DemoUser findByUsername(String username) {
        DemoUser user = users.get(username);
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户不存在或登录已失效");
        }
        return user;
    }

    public record DemoUser(
        Long id,
        String username,
        String nickname,
        List<String> roles,
        List<String> permissions
    ) {
    }
}
