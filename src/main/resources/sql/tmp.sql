-- ============================================================
-- 存量数据迁移脚本
-- 用于更新已有数据库结构
-- 执行顺序: 1. schema.sql (新建库)  2. tmp.sql (存量迁移)
-- ============================================================

-- ============================================================
-- sys_user 表新增字段
-- ============================================================
ALTER TABLE sys_user
ADD COLUMN password VARCHAR(100) COMMENT '密码（加密后）' AFTER username,
ADD COLUMN avatar VARCHAR(500) COMMENT '头像URL' AFTER phone;

-- ============================================================
-- sys_user 表新增索引
-- ============================================================
ALTER TABLE sys_user
ADD UNIQUE KEY uk_username (username);

-- ============================================================
-- sys_group 表新增索引
-- ============================================================
ALTER TABLE sys_group
ADD UNIQUE KEY uk_group_code (group_code);

-- ============================================================
-- biz_task 表新增字段
-- ============================================================
ALTER TABLE biz_task
ADD COLUMN task_code VARCHAR(50) NOT NULL COMMENT '任务编码' AFTER task_name,
ADD COLUMN source_data JSON COMMENT '监控源数据' AFTER group_id,
ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '删除标识';

-- ============================================================
-- biz_task 表新增索引
-- ============================================================
ALTER TABLE biz_task
ADD UNIQUE KEY uk_task_code (task_code);

-- ============================================================
-- biz_task_assignee 表新增字段和调整
-- ============================================================
ALTER TABLE biz_task_assignee
ADD COLUMN assignee_type TINYINT DEFAULT 1 COMMENT '关联类型：1-负责人，2-关注者' AFTER user_id,
ADD COLUMN is_main TINYINT DEFAULT 0 COMMENT '是否主要负责人：0-否，1-是' AFTER assignee_type,
ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '删除标识';

-- 调整唯一索引以支持多种 assignee_type
ALTER TABLE biz_task_assignee
DROP INDEX uk_task_user,
ADD UNIQUE KEY uk_task_user (task_id, user_id);

-- ============================================================
-- biz_type 表新增字段
-- ============================================================
ALTER TABLE biz_type
ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '删除标识';

-- ============================================================
-- biz_task_log 表新增字段
-- ============================================================
ALTER TABLE biz_task_log
ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '删除标识';

-- ============================================================
-- biz_exec_result 表新增字段
-- ============================================================
ALTER TABLE biz_exec_result
ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '删除标识';

-- ============================================================
-- biz_notify_record 表新增字段
-- ============================================================
ALTER TABLE biz_notify_record
ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '删除标识';

-- ============================================================
-- 更新现有数据的初始值
-- ============================================================

-- 给存量用户设置默认密码 (123456)
UPDATE sys_user SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH' WHERE password IS NULL;

-- 给存量任务设置 deleted = 0
UPDATE biz_task SET deleted = 0 WHERE deleted IS NULL;
UPDATE biz_task_assignee SET deleted = 0 WHERE deleted IS NULL;
UPDATE biz_type SET deleted = 0 WHERE deleted IS NULL;
UPDATE biz_task_log SET deleted = 0 WHERE deleted IS NULL;
UPDATE biz_exec_result SET deleted = 0 WHERE deleted IS NULL;
UPDATE biz_notify_record SET deleted = 0 WHERE deleted IS NULL;
