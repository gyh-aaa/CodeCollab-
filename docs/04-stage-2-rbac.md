# 第二阶段：数据库登录与 RBAC 权限

## 已完成闭环

本阶段先完成权限系统的最小可用闭环：

- 默认 `demo` 环境继续使用内存账号，方便无数据库时开发和 IDEA 直接启动。
- `mysql` 环境使用 MySQL 中的 `sys_user` 登录。
- 登录时查询用户角色、权限和可访问菜单。
- JWT 中写入用户 ID、昵称、角色、权限。
- `/api/auth/me` 返回当前用户、角色、权限和菜单。
- 前端侧边栏基于后端返回菜单渲染。

## 开发账号

初始化脚本会写入三个账号，密码统一为：

```text
Admin@123456
```

账号：

```text
admin   系统管理员
pm      项目负责人
member  研发成员
```

## 相关表

```text
sys_user
sys_role
sys_permission
sys_user_role
sys_role_permission
sys_menu
sys_role_menu
```

## 启动方式

无数据库开发，当前也是默认启动方式：

```bash
cd backend
mvn spring-boot:run
```

连接 MySQL 开发：

```bash
docker compose up -d
cd backend
mvn spring-boot:run "-Dspring-boot.run.profiles=mysql"
```

IDEA 中可以在 Run Configuration 的 `Active profiles` 填 `mysql`。数据库连接信息在 `backend/src/main/resources/application-mysql.yml`。

## 注意事项

如果你的 MySQL 容器之前已经初始化过，Docker 不会自动重新执行 `docker/mysql/init/01-schema.sql`。

这种情况下可以：

- 删除 `.docker-data/mysql` 后重新 `docker compose up -d`
- 或者手动在 MySQL 客户端执行 `docker/mysql/init/01-schema.sql`

## 下一步

第二阶段后半段建议继续做：

- 用户管理列表
- 角色管理列表
- 权限分配接口
- 前端按钮级权限指令
- 管理员专属菜单
