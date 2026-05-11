# 接口规范

## 1. 路径规范

统一使用 `/api` 作为接口前缀。

示例：

```text
POST /api/auth/login
GET  /api/auth/me
GET  /api/projects
POST /api/projects
```

## 2. 响应格式

所有接口统一返回：

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "traceId": "..."
}
```

约定：

- `code = 0` 表示成功
- `code != 0` 表示失败
- `message` 给前端展示或调试
- `data` 是业务数据
- `traceId` 用于排查日志

## 3. 分页格式

请求参数：

```text
pageNo=1&pageSize=10
```

响应数据：

```json
{
  "records": [],
  "total": 100,
  "pageNo": 1,
  "pageSize": 10
}
```

## 4. 鉴权规范

登录成功后前端在请求头中携带：

```text
Authorization: Bearer <access_token>
```

后端通过 JWT 解析用户身份和权限。

## 5. 错误码规划

```text
0       success
40000   请求参数错误
40100   未登录或 Token 无效
40300   没有访问权限
40400   资源不存在
40900   数据冲突
50000   系统异常
```
