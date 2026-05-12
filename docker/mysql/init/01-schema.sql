CREATE DATABASE IF NOT EXISTS code_collab
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE code_collab;

CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL UNIQUE,
  password_hash VARCHAR(128) NOT NULL,
  nickname VARCHAR(64) NOT NULL,
  avatar_url VARCHAR(255) NULL,
  email VARCHAR(128) NULL,
  mobile VARCHAR(32) NULL,
  status TINYINT NOT NULL DEFAULT 1,
  last_login_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_code VARCHAR(64) NOT NULL UNIQUE,
  role_name VARCHAR(64) NOT NULL,
  description VARCHAR(255) NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  permission_code VARCHAR(128) NOT NULL UNIQUE,
  permission_name VARCHAR(64) NOT NULL,
  resource_type VARCHAR(32) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_role_permission (
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  parent_id BIGINT NOT NULL DEFAULT 0,
  menu_key VARCHAR(64) NOT NULL UNIQUE,
  title VARCHAR(64) NOT NULL,
  path VARCHAR(128) NOT NULL,
  icon VARCHAR(64) NULL,
  permission_code VARCHAR(128) NULL,
  sort_order INT NOT NULL DEFAULT 0,
  visible TINYINT NOT NULL DEFAULT 1,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_role_menu (
  role_id BIGINT NOT NULL,
  menu_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS organization (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  description VARCHAR(512) NULL,
  owner_id BIGINT NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  INDEX idx_organization_owner (owner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS organization_member (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  organization_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  member_role VARCHAR(32) NOT NULL,
  joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_org_user (organization_id, user_id),
  INDEX idx_org_member_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS project (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  organization_id BIGINT NOT NULL,
  name VARCHAR(128) NOT NULL,
  code VARCHAR(32) NOT NULL,
  description VARCHAR(512) NULL,
  owner_id BIGINT NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  start_date DATE NULL,
  end_date DATE NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uk_project_org_code (organization_id, code),
  INDEX idx_project_org (organization_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS project_member (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  project_role VARCHAR(32) NOT NULL,
  joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_project_user (project_id, user_id),
  INDEX idx_project_member_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS task (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  parent_id BIGINT NULL,
  title VARCHAR(160) NOT NULL,
  description TEXT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'TODO',
  priority VARCHAR(32) NOT NULL DEFAULT 'MEDIUM',
  assignee_id BIGINT NULL,
  reporter_id BIGINT NOT NULL,
  due_date DATETIME NULL,
  sort_order INT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  INDEX idx_task_project_status (project_id, status),
  INDEX idx_task_assignee (assignee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS task_label (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  name VARCHAR(64) NOT NULL,
  color VARCHAR(32) NOT NULL DEFAULT '#409EFF',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_label_project_name (project_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS task_label_rel (
  task_id BIGINT NOT NULL,
  label_id BIGINT NOT NULL,
  PRIMARY KEY (task_id, label_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS task_comment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  task_id BIGINT NOT NULL,
  parent_id BIGINT NULL,
  author_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  INDEX idx_comment_task (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS file_resource (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  biz_type VARCHAR(32) NOT NULL,
  biz_id BIGINT NOT NULL,
  original_name VARCHAR(255) NOT NULL,
  object_name VARCHAR(255) NOT NULL,
  content_type VARCHAR(128) NULL,
  size_bytes BIGINT NOT NULL,
  uploader_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_file_biz (biz_type, biz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS notification (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  receiver_id BIGINT NOT NULL,
  title VARCHAR(128) NOT NULL,
  content VARCHAR(512) NOT NULL,
  notification_type VARCHAR(32) NOT NULL,
  biz_type VARCHAR(32) NULL,
  biz_id BIGINT NULL,
  read_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_notification_receiver (receiver_id, read_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS operation_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  operator_id BIGINT NOT NULL,
  biz_type VARCHAR(32) NOT NULL,
  biz_id BIGINT NOT NULL,
  action VARCHAR(64) NOT NULL,
  detail VARCHAR(1024) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_operation_biz (biz_type, biz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO sys_user (id, username, password_hash, nickname, email, status)
VALUES
  (1, 'admin', '{noop}Admin@123456', '系统管理员', 'admin@codecollab.local', 1),
  (2, 'pm', '{noop}Admin@123456', '项目负责人', 'pm@codecollab.local', 1),
  (3, 'member', '{noop}Admin@123456', '研发成员', 'member@codecollab.local', 1)
ON DUPLICATE KEY UPDATE
  nickname = VALUES(nickname),
  email = VALUES(email),
  status = VALUES(status);

INSERT INTO sys_role (id, role_code, role_name, description, status)
VALUES
  (1, 'ADMIN', '系统管理员', '拥有系统全部权限', 1),
  (2, 'PROJECT_MANAGER', '项目负责人', '可管理项目和任务', 1),
  (3, 'MEMBER', '普通成员', '可查看项目并处理任务', 1)
ON DUPLICATE KEY UPDATE
  role_name = VALUES(role_name),
  description = VALUES(description),
  status = VALUES(status);

INSERT INTO sys_permission (id, permission_code, permission_name, resource_type)
VALUES
  (1, '*:*:*', '全部权限', 'SYSTEM'),
  (2, 'dashboard:view', '查看工作台', 'MENU'),
  (3, 'project:read', '查看项目', 'API'),
  (4, 'project:write', '管理项目', 'API'),
  (5, 'task:read', '查看任务', 'API'),
  (6, 'task:write', '管理任务', 'API'),
  (7, 'notification:read', '查看通知', 'API')
ON DUPLICATE KEY UPDATE
  permission_name = VALUES(permission_name),
  resource_type = VALUES(resource_type);

INSERT INTO sys_menu (id, parent_id, menu_key, title, path, icon, permission_code, sort_order, visible, status)
VALUES
  (1, 0, 'dashboard', '工作台', '/dashboard', 'House', 'dashboard:view', 10, 1, 1),
  (2, 0, 'projects', '项目', '/projects', 'Folder', 'project:read', 20, 1, 1),
  (3, 0, 'board', '任务看板', '/board', 'Grid', 'task:read', 30, 1, 1),
  (4, 0, 'notifications', '通知', '/notifications', 'Bell', 'notification:read', 40, 1, 1)
ON DUPLICATE KEY UPDATE
  title = VALUES(title),
  path = VALUES(path),
  icon = VALUES(icon),
  permission_code = VALUES(permission_code),
  sort_order = VALUES(sort_order),
  visible = VALUES(visible),
  status = VALUES(status);

INSERT IGNORE INTO sys_user_role (user_id, role_id)
VALUES
  (1, 1),
  (2, 2),
  (3, 3);

INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
VALUES
  (1, 1),
  (1, 2),
  (1, 3),
  (1, 4),
  (1, 5),
  (1, 6),
  (1, 7),
  (2, 2),
  (2, 3),
  (2, 4),
  (2, 5),
  (2, 6),
  (2, 7),
  (3, 2),
  (3, 3),
  (3, 5),
  (3, 7);

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
VALUES
  (1, 1),
  (1, 2),
  (1, 3),
  (1, 4),
  (2, 1),
  (2, 2),
  (2, 3),
  (2, 4),
  (3, 1),
  (3, 2),
  (3, 3),
  (3, 4);
