package com.codecollab.modules.system.mapper;

import com.codecollab.modules.system.entity.SysMenu;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RbacMapper {

    @Select("""
        SELECT r.role_code
        FROM sys_role r
        INNER JOIN sys_user_role ur ON ur.role_id = r.id
        WHERE ur.user_id = #{userId}
          AND r.status = 1
          AND r.deleted = 0
        GROUP BY r.id, r.role_code
        ORDER BY r.id ASC
        """)
    List<String> selectRoleCodesByUserId(Long userId);

    @Select("""
        SELECT p.permission_code
        FROM sys_permission p
        INNER JOIN sys_role_permission rp ON rp.permission_id = p.id
        INNER JOIN sys_user_role ur ON ur.role_id = rp.role_id
        INNER JOIN sys_role r ON r.id = ur.role_id
        WHERE ur.user_id = #{userId}
          AND r.status = 1
          AND r.deleted = 0
        GROUP BY p.id, p.permission_code
        ORDER BY p.id ASC
        """)
    List<String> selectPermissionCodesByUserId(Long userId);

    @Select("""
        SELECT DISTINCT m.id,
               m.parent_id,
               m.menu_key,
               m.title,
               m.path,
               m.icon,
               m.permission_code,
               m.sort_order,
               m.visible,
               m.status
        FROM sys_menu m
        INNER JOIN sys_role_menu rm ON rm.menu_id = m.id
        INNER JOIN sys_user_role ur ON ur.role_id = rm.role_id
        INNER JOIN sys_role r ON r.id = ur.role_id
        WHERE ur.user_id = #{userId}
          AND r.status = 1
          AND r.deleted = 0
          AND m.visible = 1
          AND m.status = 1
        ORDER BY m.sort_order ASC, m.id ASC
        """)
    List<SysMenu> selectMenusByUserId(Long userId);
}
