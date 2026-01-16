-- ============================================================
-- Operation Monitor Database Schema
-- Database: MySQL 5.7+
-- ============================================================

-- Drop tables in reverse order of dependencies (child tables first)
DROP TABLE IF EXISTS biz_notify_record;
DROP TABLE IF EXISTS biz_exec_result;
DROP TABLE IF EXISTS biz_task_log;
DROP TABLE IF EXISTS biz_task_assignee;
DROP TABLE IF EXISTS biz_task;
DROP TABLE IF EXISTS biz_type;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_group;

-- ============================================================
-- Table: sys_group (小组表)
-- ============================================================
CREATE TABLE sys_group (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    group_name VARCHAR(100) NOT NULL COMMENT '小组名称',
    group_code VARCHAR(50) NOT NULL COMMENT '小组编码',
    description VARCHAR(500) COMMENT '小组描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_user BIGINT COMMENT '创建用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_user BIGINT COMMENT '更新用户ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
    UNIQUE KEY uk_group_code (group_code),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='小组表';

-- ============================================================
-- Table: sys_user (用户表)
-- ============================================================
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    real_name VARCHAR(100) COMMENT '真实姓名',
    email VARCHAR(200) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    group_id BIGINT COMMENT '所属小组ID',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_user BIGINT COMMENT '创建用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_user BIGINT COMMENT '更新用户ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
    UNIQUE KEY uk_username (username),
    KEY idx_group_id (group_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- ============================================================
-- Table: biz_type (类型表)
-- ============================================================
CREATE TABLE biz_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    type_name VARCHAR(100) NOT NULL COMMENT '类型名称',
    description VARCHAR(500) COMMENT '类型描述',
    sql_content TEXT COMMENT 'SQL内容',
    field_config JSON COMMENT '字段配置（JSON格式）',
    verify_config JSON COMMENT '验证配置（JSON格式）',
    formula VARCHAR(500) COMMENT '计算公式',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_user BIGINT COMMENT '创建用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_user BIGINT COMMENT '更新用户ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='业务类型表';

-- ============================================================
-- Table: biz_task (任务表)
-- ============================================================
CREATE TABLE biz_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    type_id BIGINT NOT NULL COMMENT '类型ID',
    task_name VARCHAR(100) NOT NULL COMMENT '任务名称',
    task_code VARCHAR(50) NOT NULL COMMENT '任务编码',
    description VARCHAR(500) COMMENT '任务描述',
    threshold_rules JSON COMMENT '阈值规则配置（JSON格式）',
    complete_rules JSON COMMENT '完成规则配置（JSON格式）',
    query_condition JSON COMMENT '查询条件配置（JSON格式）',
    formula VARCHAR(500) COMMENT '计算公式',
    status TINYINT DEFAULT 0 COMMENT '状态：0-暂停，1-运行中，2-已完成',
    last_exec_time DATETIME COMMENT '最后执行时间',
    next_exec_time DATETIME COMMENT '下次执行时间',
    create_user BIGINT COMMENT '创建用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_user BIGINT COMMENT '更新用户ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
    assignee_id BIGINT COMMENT '任务负责人ID',
    group_id BIGINT COMMENT '任务负责组ID',
    source_data JSON COMMENT '监控源数据（如api_slow_stat、biz_metrics_stat等表的数据）',
    UNIQUE KEY uk_task_code (task_code),
    KEY idx_type_id (type_id),
    KEY idx_status (status),
    KEY idx_assignee_id (assignee_id),
    KEY idx_group_id (group_id),
    KEY idx_next_exec_time (next_exec_time),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务表';

-- ============================================================
-- Table: biz_task_assignee (任务-负责人关联表)
-- ============================================================
CREATE TABLE biz_task_assignee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    assignee_type TINYINT DEFAULT 1 COMMENT '关联类型：1-负责人，2-关注者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
    UNIQUE KEY uk_task_user (task_id, user_id),
    KEY idx_task_id (task_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务-负责人关联表';

-- ============================================================
-- Table: biz_task_log (任务执行日志表)
-- ============================================================
CREATE TABLE biz_task_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    type_id BIGINT COMMENT '类型ID',
    exec_time DATETIME COMMENT '执行时间',
    exec_status TINYINT COMMENT '执行状态：0-失败，1-成功',
    trigger_type TINYINT COMMENT '触发类型：1-定时，2-手动，3-API调用',
    exec_sql TEXT COMMENT '执行的SQL语句',
    exec_result JSON COMMENT '执行结果（JSON格式）',
    error_msg TEXT COMMENT '错误信息',
    duration_ms INT COMMENT '执行耗时（毫秒）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
    KEY idx_task_id (task_id),
    KEY idx_type_id (type_id),
    KEY idx_exec_time (exec_time),
    KEY idx_exec_status (exec_status),
    KEY idx_trigger_type (trigger_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务执行日志表';

-- ============================================================
-- Table: biz_exec_result (执行结果表)
-- ============================================================
CREATE TABLE biz_exec_result (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    type_id BIGINT COMMENT '类型ID',
    exec_time DATETIME COMMENT '执行时间',
    data_count INT COMMENT '数据条数',
    metric_data JSON COMMENT '指标数据（JSON格式）',
    threshold_result JSON COMMENT '阈值检查结果（JSON格式）',
    is_completed TINYINT DEFAULT 0 COMMENT '是否完成：0-未完成，1-已完成',
    complete_reason VARCHAR(500) COMMENT '完成原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
    KEY idx_task_id (task_id),
    KEY idx_type_id (type_id),
    KEY idx_exec_time (exec_time),
    KEY idx_is_completed (is_completed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='执行结果表';

-- ============================================================
-- Table: biz_notify_record (通知记录表)
-- ============================================================
CREATE TABLE biz_notify_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    task_id BIGINT COMMENT '任务ID',
    task_name VARCHAR(100) COMMENT '任务名称',
    channel VARCHAR(50) COMMENT '通知渠道：dingtalk, email, webhook',
    notify_type TINYINT COMMENT '通知类型：1-阈值告警，2-任务完成，3-自定义',
    content JSON COMMENT '通知内容（JSON格式）',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待发送，1-已发送，2-失败，3-重试中',
    error_msg TEXT COMMENT '错误信息（如果失败）',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    send_time DATETIME COMMENT '发送时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
    KEY idx_task_id (task_id),
    KEY idx_channel (channel),
    KEY idx_notify_type (notify_type),
    KEY idx_status (status),
    KEY idx_send_time (send_time),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='通知记录表';

-- ============================================================
-- Add Foreign Key Constraints (Optional)
-- ============================================================
-- ALTER TABLE biz_task ADD CONSTRAINT fk_task_type FOREIGN KEY (type_id) REFERENCES biz_type(id);
-- ALTER TABLE biz_task ADD CONSTRAINT fk_task_assignee FOREIGN KEY (assignee_id) REFERENCES sys_user(id);
-- ALTER TABLE biz_task ADD CONSTRAINT fk_task_group FOREIGN KEY (group_id) REFERENCES sys_group(id);
-- ALTER TABLE biz_task_log ADD CONSTRAINT fk_log_task FOREIGN KEY (task_id) REFERENCES biz_task(id);
-- ALTER TABLE biz_task_log ADD CONSTRAINT fk_log_type FOREIGN KEY (type_id) REFERENCES biz_type(id);
-- ALTER TABLE biz_exec_result ADD CONSTRAINT fk_result_task FOREIGN KEY (task_id) REFERENCES biz_task(id);
-- ALTER TABLE biz_exec_result ADD CONSTRAINT fk_result_type FOREIGN KEY (type_id) REFERENCES biz_type(id);
-- ALTER TABLE biz_notify_record ADD CONSTRAINT fk_notify_task FOREIGN KEY (task_id) REFERENCES biz_task(id);
-- ALTER TABLE biz_task_assignee ADD CONSTRAINT fk_assignee_task FOREIGN KEY (task_id) REFERENCES biz_task(id);
-- ALTER TABLE biz_task_assignee ADD CONSTRAINT fk_assignee_user FOREIGN KEY (user_id) REFERENCES sys_user(id);
-- ALTER TABLE sys_user ADD CONSTRAINT fk_user_group FOREIGN KEY (group_id) REFERENCES sys_group(id);

-- ============================================================
-- Table: api_slow_stat (慢接口统计表)
-- ============================================================
DROP TABLE IF EXISTS api_slow_stat;
CREATE TABLE api_slow_stat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    api VARCHAR(200) NOT NULL COMMENT '接口路径',
    total INT NOT NULL COMMENT '总请求数',
    slow_count INT NOT NULL COMMENT '慢请求数量',
    date DATE NOT NULL COMMENT '统计日期',
    slow_value DECIMAL(10,2) COMMENT '慢请求耗时总值(ms)',
    slow_rate DECIMAL(5,2) COMMENT '慢请求占比(%)',
    total_avg DECIMAL(10,2) COMMENT '平均响应时间(ms)',
    slow_avg DECIMAL(10,2) COMMENT '慢请求平均耗时(ms)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_api (api),
    KEY idx_date (date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='慢接口统计表';

-- ============================================================
-- Table: biz_metrics_stat (业务指标统计表)
-- ============================================================
DROP TABLE IF EXISTS biz_metrics_stat;
CREATE TABLE biz_metrics_stat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    metric_name VARCHAR(100) NOT NULL COMMENT '指标名称',
    category VARCHAR(50) NOT NULL COMMENT '指标类别',
    metric_value DECIMAL(15,2) NOT NULL COMMENT '指标值',
    compare_value DECIMAL(15,2) COMMENT '对比值',
    change_rate DECIMAL(8,4) COMMENT '变化率(%)',
    date DATE NOT NULL COMMENT '统计日期',
    threshold_min DECIMAL(15,2) COMMENT '最小阈值',
    threshold_max DECIMAL(15,2) COMMENT '最大阈值',
    status TINYINT DEFAULT 1 COMMENT '状态：0-异常，1-正常',
    unit VARCHAR(20) COMMENT '单位',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_metric_name (metric_name),
    KEY idx_category (category),
    KEY idx_date (date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='业务指标统计表';
