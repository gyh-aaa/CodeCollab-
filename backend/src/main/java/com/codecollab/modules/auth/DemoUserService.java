package com.codecollab.modules.auth;

import com.codecollab.common.BusinessException;
import com.codecollab.common.ErrorCode;
import com.codecollab.modules.auth.dto.MenuResponse;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("demo")
public class DemoUserService implements UserAccountService {

    private static final String DEMO_PASSWORD = "Admin@123456";

    private final Map<String, AuthenticatedUser> users = Map.of(
        "admin", new AuthenticatedUser(1L, "admin", "系统管理员", List.of("ADMIN"), List.of("*:*:*"), defaultMenus()),
        "pm", new AuthenticatedUser(2L, "pm", "项目负责人", List.of("PROJECT_MANAGER"), List.of("project:read", "task:write"), defaultMenus()),
        "member", new AuthenticatedUser(3L, "member", "研发成员", List.of("MEMBER"), List.of("project:read", "task:read"), defaultMenus())
    );

    @Override
    public AuthenticatedUser authenticate(String username, String password) {
        AuthenticatedUser user = users.get(username);
        if (user == null || !DEMO_PASSWORD.equals(password)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "账号或密码错误");
        }
        return user;
    }

    @Override
    public AuthenticatedUser findByUsername(String username) {
        AuthenticatedUser user = users.get(username);
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户不存在或登录已失效");
        }
        return user;
    }

    private static List<MenuResponse> defaultMenus() {
        return List.of(
            new MenuResponse("dashboard", "工作台", "/dashboard", "House", "dashboard:view"),
            new MenuResponse("projects", "项目", "/projects", "Folder", "project:read"),
            new MenuResponse("board", "任务看板", "/board", "Grid", "task:read"),
            new MenuResponse("notifications", "通知", "/notifications", "Bell", "notification:read")
        );
    }
}
