-- ============================================================
-- 用户登录表
-- 运行方式: mysql -u root -p cornea_patient_management < sql/add_users_table.sql
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id              INT             AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)     NOT NULL        COMMENT '用户名',
    password_hash   VARCHAR(255)    NOT NULL        COMMENT '密码哈希(BCrypt)',
    display_name    VARCHAR(100)                    COMMENT '显示名称',
    role            VARCHAR(20)     DEFAULT 'user'  COMMENT '角色(admin/user)',
    token           VARCHAR(64)                     COMMENT '登录令牌',
    created_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_token (token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户登录表';
