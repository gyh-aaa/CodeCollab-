package com.codecollab.modules.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codecollab.common.BusinessException;
import com.codecollab.common.ErrorCode;
import com.codecollab.modules.auth.dto.MenuResponse;
import com.codecollab.modules.system.entity.SysMenu;
import com.codecollab.modules.system.entity.SysUser;
import com.codecollab.modules.system.mapper.RbacMapper;
import com.codecollab.modules.system.mapper.SysUserMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("mysql")
public class DatabaseUserAccountService implements UserAccountService {

    private static final Logger log = LoggerFactory.getLogger(DatabaseUserAccountService.class);

    private final SysUserMapper sysUserMapper;
    private final RbacMapper rbacMapper;
    private final PasswordEncoder passwordEncoder;

    public DatabaseUserAccountService(
        SysUserMapper sysUserMapper,
        RbacMapper rbacMapper,
        PasswordEncoder passwordEncoder
    ) {
        this.sysUserMapper = sysUserMapper;
        this.rbacMapper = rbacMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public AuthenticatedUser authenticate(String username, String password) {
        try {
            SysUser user = findActiveUser(username);
            if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "账号或密码错误");
            }

            SysUser update = new SysUser();
            update.setId(user.getId());
            update.setLastLoginAt(LocalDateTime.now());
            sysUserMapper.updateById(update);

            return toAuthenticatedUser(user);
        } catch (DataAccessException exception) {
            log.error("Database login failed for username={}", username, exception);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "数据库登录失败，请检查 MySQL 账号密码、code_collab 数据库和 RBAC 初始化脚本");
        }
    }

    @Override
    public AuthenticatedUser findByUsername(String username) {
        try {
            return toAuthenticatedUser(findActiveUser(username));
        } catch (DataAccessException exception) {
            log.error("Database user lookup failed for username={}", username, exception);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "数据库查询用户失败，请检查 MySQL 连接和 RBAC 初始化脚本");
        }
    }

    private SysUser findActiveUser(String username) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
            .eq(SysUser::getUsername, username)
            .eq(SysUser::getDeleted, 0)
            .last("LIMIT 1"));
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户不存在或登录已失效");
        }
        if (!Integer.valueOf(1).equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "用户已被禁用");
        }
        return user;
    }

    private AuthenticatedUser toAuthenticatedUser(SysUser user) {
        List<String> roles = rbacMapper.selectRoleCodesByUserId(user.getId());
        List<String> permissions = rbacMapper.selectPermissionCodesByUserId(user.getId());
        List<MenuResponse> menus = rbacMapper.selectMenusByUserId(user.getId())
            .stream()
            .map(this::toMenuResponse)
            .toList();

        return new AuthenticatedUser(
            user.getId(),
            user.getUsername(),
            user.getNickname(),
            roles,
            permissions,
            menus
        );
    }

    private MenuResponse toMenuResponse(SysMenu menu) {
        return new MenuResponse(
            menu.getMenuKey(),
            menu.getTitle(),
            menu.getPath(),
            menu.getIcon(),
            menu.getPermissionCode()
        );
    }
}
