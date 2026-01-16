-- ============================================================
-- Operation Monitor Sample Data
-- ============================================================

-- ============================================================
-- Insert Sample Groups (sys_group)
-- ============================================================
INSERT INTO sys_group (group_name, group_code, description, status, create_user, update_user) VALUES
('运维一组', 'ops-team-1', '负责核心业务系统运维', 1, 1, 1),
('运维二组', 'ops-team-2', '负责基础设施运维', 1, 1, 1),
('数据组', 'data-team', '负责数据平台和数据监控', 1, 1, 1);

-- ============================================================
-- Insert Sample Users (sys_user)
-- ============================================================
INSERT INTO sys_user (username, real_name, email, phone, group_id, status, create_user, update_user) VALUES
('admin', '系统管理员', 'admin@example.com', '13800000001', 1, 1, 1, 1),
('zhangsan', '张三', 'zhangsan@example.com', '13800000002', 1, 1, 1, 1),
('lisi', '李四', 'lisi@example.com', '13800000003', 2, 1, 1, 1),
('wangwu', '王五', 'wangwu@example.com', '13800000004', 3, 1, 1, 1),
('zhaoliu', '赵六', 'zhaoliu@example.com', '13800000005', 3, 1, 1, 1);

-- ============================================================
-- Insert Sample BizTypes (biz_type)
-- ============================================================
INSERT INTO biz_type (type_name, type_code, description, sql_content, formula, status, create_user, update_user) VALUES
('慢接口查询', 'slow_query', '监控慢接口查询性能', 'SELECT id, request_uri, AVG(request_time) as avg_time, COUNT(*) as query_count FROM api_request_log WHERE request_time > #{startTime} AND request_time < #{endTime} AND request_time > 1000 GROUP BY id, request_uri ORDER BY avg_time DESC LIMIT 100', 'AVG(request_time)', 1, 1, 1),
('业务指标监控', 'biz_metrics', '监控核心业务指标', 'SELECT product_id, SUM(order_amount) as total_amount, COUNT(DISTINCT user_id) as user_count, COUNT(*) as order_count FROM orders WHERE order_time > #{startTime} AND order_time < #{endTime} GROUP BY product_id', 'SUM(order_amount)', 1, 1, 1),
('异常检测', 'exception_detect', '检测系统异常情况', 'SELECT exception_type, COUNT(*) as exception_count, COUNT(DISTINCT service_name) as service_count FROM exception_log WHERE log_time > #{startTime} AND log_time < #{endTime} GROUP BY exception_type HAVING exception_count > 10', 'COUNT(*)', 1, 1, 1),
('数据库连接池监控', 'db_pool_monitor', '监控数据库连接池使用情况', 'SELECT pool_name, MAX(active_connections) as max_active, AVG(active_connections) as avg_active, MAX(waiting_threads) as max_waiting FROM db_pool_stats WHERE stat_time > #{startTime} AND stat_time < #{endTime} GROUP BY pool_name', 'MAX(active_connections)', 1, 1, 1),
('消息队列积压监控', 'mq_backlog', '监控消息队列积压情况', 'SELECT queue_name, SUM(un_consume_count) as total_backlog, AVG(produce_rate) as avg_produce_rate, AVG(consume_rate) as avg_consume_rate FROM mq_stats WHERE stat_time > #{startTime} AND stat_time < #{endTime} GROUP BY queue_name', 'SUM(un_consume_count)', 1, 1, 1);

-- ============================================================
-- Insert Sample BizTasks (biz_task)
-- ============================================================
INSERT INTO biz_task (type_id, task_name, task_code, description, status, next_exec_time, create_user, update_user, assignee_id, group_id) VALUES
(1, '核心接口慢查询监控', 'core_api_slow_query', '监控核心API接口的慢查询情况，每5分钟执行一次', 1, DATE_ADD(NOW(), INTERVAL 5 MINUTE), 1, 1, 2, 1),
(1, '非核心接口慢查询监控', 'non_core_api_slow_query', '监控非核心API接口的慢查询情况，每10分钟执行一次', 1, DATE_ADD(NOW(), INTERVAL 10 MINUTE), 1, 1, 2, 1),
(2, '订单金额监控', 'order_amount_monitor', '监控订单金额指标，每小时检查一次', 1, DATE_ADD(NOW(), INTERVAL 1 HOUR), 1, 1, 4, 3),
(2, '用户活跃度监控', 'user_activity_monitor', '监控用户活跃度指标，每30分钟检查一次', 1, DATE_ADD(NOW(), INTERVAL 30 MINUTE), 1, 1, 4, 3),
(3, '异常告警监控', 'exception_alert', '实时监控异常日志，发现异常及时告警', 1, DATE_ADD(NOW(), INTERVAL 1 MINUTE), 1, 1, 3, 2),
(3, '服务健康检查', 'service_health_check', '定期检查各服务的健康状态', 1, DATE_ADD(NOW(), INTERVAL 5 MINUTE), 1, 1, 3, 2),
(4, '数据库连接池告警', 'db_pool_alert', '监控数据库连接池使用情况，超过阈值告警', 1, DATE_ADD(NOW(), INTERVAL 2 MINUTE), 1, 1, 2, 1),
(5, '消息队列积压告警', 'mq_backlog_alert', '监控消息队列积压情况，积压过多告警', 1, DATE_ADD(NOW(), INTERVAL 3 MINUTE), 1, 1, 5, 3);

-- ============================================================
-- Insert Sample Task Assignees (biz_task_assignee)
-- ============================================================
INSERT INTO biz_task_assignee (task_id, user_id, assignee_type) VALUES
(1, 2, 1),  -- 任务1，张三是负责人
(1, 3, 2),  -- 任务1，李四是关注者
(2, 2, 1),  -- 任务2，张三是负责人
(3, 4, 1),  -- 任务3，王五是负责人
(3, 5, 2),  -- 任务3，赵六是关注者
(4, 4, 1),  -- 任务4，王五是负责人
(5, 3, 1),  -- 任务5，李四是负责人
(6, 3, 1),  -- 任务6，李四是负责人
(7, 2, 1),  -- 任务7，张三是负责人
(8, 5, 1);  -- 任务8，赵六是负责人

-- ============================================================
-- Insert Sample Task Logs (biz_task_log)
-- ============================================================
INSERT INTO biz_task_log (task_id, type_id, exec_time, exec_status, trigger_type, exec_sql, exec_result, error_msg, duration_ms, create_time) VALUES
(1, 1, NOW(), 1, 1, 'SELECT id, request_uri, AVG(request_time) as avg_time FROM api_request_log WHERE request_time > ''2024-01-01 00:00:00'' AND request_time < ''2024-01-01 00:05:00'' GROUP BY id, request_uri', '{"totalQueries": 150, "slowQueries": 12}', NULL, 1250, NOW()),
(2, 1, NOW(), 1, 1, 'SELECT id, request_uri, AVG(request_time) as avg_time FROM api_request_log WHERE request_time > ''2024-01-01 00:00:00'' AND request_time < ''2024-01-01 00:10:00'' GROUP BY id, request_uri', '{"totalQueries": 300, "slowQueries": 25}', NULL, 2100, NOW()),
(3, 2, NOW(), 1, 1, 'SELECT product_id, SUM(order_amount) as total_amount FROM orders WHERE order_time > ''2024-01-01 00:00:00'' AND order_time < ''2024-01-01 01:00:00'' GROUP BY product_id', '{"totalOrders": 5000, "totalAmount": 150000.00}', NULL, 3500, NOW()),
(4, 2, NOW(), 1, 1, 'SELECT COUNT(DISTINCT user_id) as user_count FROM orders WHERE order_time > ''2024-01-01 00:00:00'' AND order_time < ''2024-01-01 00:30:00''', '{"activeUsers": 1200}', NULL, 1800, NOW()),
(5, 3, NOW(), 0, 1, 'SELECT exception_type, COUNT(*) as exception_count FROM exception_log', NULL, 'Connection timeout to database', 30000, NOW()),
(6, 3, NOW(), 1, 1, 'SELECT service_name, COUNT(*) as error_count FROM service_health_check WHERE status != ''healthy'' GROUP BY service_name', '{"healthyServices": 15, "unhealthyServices": 2}', NULL, 800, NOW());

-- ============================================================
-- Insert Sample Exec Results (biz_exec_result)
-- ============================================================
INSERT INTO biz_exec_result (task_id, type_id, exec_time, data_count, metric_data, threshold_result, is_completed, complete_reason, create_time) VALUES
(1, 1, NOW(), 150, '{"avgResponseTime": 125, "maxResponseTime": 2500, "p95ResponseTime": 800}', '{"threshold": 1000, "exceededCount": 12, "alertLevel": "WARNING"}', 0, NULL, NOW()),
(2, 1, NOW(), 300, '{"avgResponseTime": 85, "maxResponseTime": 1800, "p95ResponseTime": 500}', '{"threshold": 2000, "exceededCount": 5, "alertLevel": "NORMAL"}', 0, NULL, NOW()),
(3, 2, NOW(), 50, '{"totalAmount": 150000.00, "totalOrders": 5000, "avgOrderAmount": 30.00}', '{"threshold": 100000, "isExceeded": true, "alertLevel": "INFO"}', 0, NULL, NOW()),
(4, 2, NOW(), 1, '{"activeUsers": 1200, "newUsers": 150, "retentionRate": 0.85}', '{"threshold": 1000, "isExceeded": true, "alertLevel": "NORMAL"}', 0, NULL, NOW()),
(5, 3, NOW(), 0, NULL, NULL, 0, 'Database connection failed', NOW()),
(6, 3, NOW(), 17, '{"healthyCount": 15, "unhealthyCount": 2, "services": [{"name": "order-service", "status": "healthy"}, {"name": "user-service", "status": "unhealthy"}]}', '{"threshold": 5, "unhealthyCount": 2, "alertLevel": "WARNING"}', 0, NULL, NOW());

-- ============================================================
-- Insert Sample Notify Records (biz_notify_record)
-- ============================================================
INSERT INTO biz_notify_record (task_id, task_name, channel, notify_type, content, status, error_msg, retry_count, send_time, create_time, update_time) VALUES
(1, '核心接口慢查询监控', 'dingtalk', 1, '{"title": "慢查询告警", "content": "发现12条慢查询，最大响应时间2500ms", "level": "WARNING"}', 1, NULL, 0, NOW(), NOW(), NOW()),
(1, '核心接口慢查询监控', 'email', 1, '{"title": "慢查询告警", "content": "发现12条慢查询，请及时处理", "level": "WARNING"}', 1, NULL, 0, NOW(), NOW(), NOW()),
(3, '订单金额监控', 'dingtalk', 2, '{"title": "任务完成", "content": "订单金额监控任务执行完成，检测到订单金额超过阈值", "level": "INFO"}', 1, NULL, 0, NOW(), NOW(), NOW()),
(5, '异常告警监控', 'dingtalk', 1, '{"title": "执行失败", "content": "异常告警监控任务执行失败：数据库连接超时", "level": "CRITICAL"}', 2, 'Connection timeout', 3, NULL, NOW(), NOW()),
(5, '异常告警监控', 'webhook', 1, '{"title": "执行失败", "content": "异常告警监控任务执行失败：数据库连接超时", "level": "CRITICAL"}', 1, NULL, 0, NOW(), NOW(), NOW()),
(6, '服务健康检查', 'dingtalk', 1, '{"title": "健康检查告警", "content": "发现2个服务不健康：order-service, user-service", "level": "WARNING"}', 1, NULL, 0, NOW(), NOW(), NOW());

-- ============================================================
-- Insert Sample Api Slow Stats (api_slow_stat) - 50 records
-- ============================================================
INSERT INTO api_slow_stat (api, total, slow_count, date, slow_value, slow_rate, total_avg, slow_avg) VALUES
('/api/user/login', 12500, 89, '2025-01-01', 125800.50, 0.71, 156.32, 1413.49),
('/api/user/login', 13200, 102, '2025-01-02', 138900.25, 0.77, 162.18, 1361.77),
('/api/user/login', 11800, 78, '2025-01-03', 102500.00, 0.66, 148.55, 1314.10),
('/api/user/login', 14500, 125, '2025-01-04', 168750.80, 0.86, 175.23, 1350.01),
('/api/user/login', 13800, 95, '2025-01-05', 128900.50, 0.69, 165.42, 1356.84),
('/api/order/create', 5800, 156, '2025-01-01', 312500.00, 2.69, 285.67, 2003.21),
('/api/order/create', 6200, 189, '2025-01-02', 425800.75, 3.05, 312.45, 2252.91),
('/api/order/create', 5500, 142, '2025-01-03', 298500.25, 2.58, 278.90, 2102.11),
('/api/order/create', 6900, 198, '2025-01-04', 456800.00, 2.87, 325.18, 2307.07),
('/api/order/create', 6400, 172, '2025-01-05', 385600.50, 2.69, 305.22, 2241.86),
('/api/order/list', 8900, 45, '2025-01-01', 58500.00, 0.51, 98.32, 1300.00),
('/api/order/list', 9200, 52, '2025-01-02', 68900.75, 0.57, 102.18, 1325.00),
('/api/order/list', 8500, 38, '2025-01-03', 49200.50, 0.45, 95.45, 1294.74),
('/api/order/list', 9800, 58, '2025-01-04', 78500.00, 0.59, 108.75, 1353.45),
('/api/order/list', 9400, 48, '2025-01-05', 64500.25, 0.51, 105.22, 1343.75),
('/api/product/detail', 15200, 62, '2025-01-01', 80600.00, 0.41, 75.23, 1300.00),
('/api/product/detail', 15800, 75, '2025-01-02', 98750.50, 0.47, 78.92, 1316.67),
('/api/product/detail', 14500, 55, '2025-01-03', 71500.00, 0.38, 72.18, 1300.00),
('/api/product/detail', 16800, 88, '2025-01-04', 118800.75, 0.52, 82.45, 1350.01),
('/api/product/detail', 16200, 72, '2025-01-05', 93600.00, 0.44, 80.12, 1300.00),
('/api/cart/add', 4500, 25, '2025-01-01', 32500.00, 0.56, 65.42, 1300.00),
('/api/cart/add', 4800, 32, '2025-01-02', 42200.75, 0.67, 68.95, 1318.75),
('/api/cart/add', 4200, 21, '2025-01-03', 27300.50, 0.50, 62.18, 1300.00),
('/api/cart/add', 5100, 38, '2025-01-04', 50750.00, 0.75, 72.45, 1335.53),
('/api/cart/add', 4900, 28, '2025-01-05', 36400.25, 0.57, 69.22, 1300.00),
('/api/payment/pay', 3200, 198, '2025-01-01', 495000.00, 6.19, 456.23, 2500.00),
('/api/payment/pay', 3500, 225, '2025-01-02', 576250.50, 6.43, 485.67, 2561.11),
('/api/payment/pay', 2900, 178, '2025-01-03', 445000.75, 6.14, 432.18, 2500.00),
('/api/payment/pay', 3800, 248, '2025-01-04', 632000.00, 6.53, 512.45, 2548.39),
('/api/payment/pay', 3600, 215, '2025-01-05', 537500.25, 5.97, 498.22, 2500.00),
('/api/user/profile', 6800, 18, '2025-01-01', 23400.00, 0.26, 52.18, 1300.00),
('/api/user/profile', 7200, 22, '2025-01-02', 28600.50, 0.31, 55.42, 1300.00),
('/api/user/profile', 6500, 15, '2025-01-03', 19500.75, 0.23, 49.92, 1300.00),
('/api/user/profile', 7500, 25, '2025-01-04', 32500.00, 0.33, 58.75, 1300.00),
('/api/user/profile', 7100, 20, '2025-01-05', 26000.50, 0.28, 54.18, 1300.00),
('/api/search/list', 18500, 245, '2025-01-01', 318500.00, 1.32, 125.67, 1300.00),
('/api/search/list', 19200, 268, '2025-01-02', 348400.75, 1.40, 132.18, 1300.00),
('/api/search/list', 17800, 225, '2025-01-03', 292500.50, 1.26, 118.92, 1300.00),
('/api/search/list', 20500, 298, '2025-01-04', 387400.00, 1.45, 142.45, 1300.00),
('/api/search/list', 19800, 275, '2025-01-05', 357500.25, 1.39, 138.22, 1300.00),
('/api/notification/list', 9200, 35, '2025-01-01', 45500.00, 0.38, 85.42, 1300.00),
('/api/notification/list', 9800, 42, '2025-01-02', 54600.75, 0.43, 89.18, 1300.00),
('/api/notification/list', 8800, 28, '2025-01-03', 36400.50, 0.32, 82.22, 1300.00),
('/api/notification/list', 10500, 48, '2025-01-04', 62400.00, 0.46, 95.75, 1300.00),
('/api/notification/list', 10100, 40, '2025-01-05', 52000.25, 0.40, 92.18, 1300.00),
('/api/analytics/report', 1500, 95, '2025-01-01', 237500.00, 6.33, 685.23, 2500.00),
('/api/analytics/report', 1650, 112, '2025-01-02', 280000.50, 6.79, 725.67, 2500.00),
('/api/analytics/report', 1380, 85, '2025-01-03', 212500.25, 6.16, 652.18, 2500.00),
('/api/analytics/report', 1800, 125, '2025-01-04', 312500.75, 6.94, 758.45, 2500.00),
('/api/analytics/report', 1720, 108, '2025-01-05', 270000.50, 6.28, 715.22, 2500.00);

-- ============================================================
-- Insert Sample Biz Metrics Stats (biz_metrics_stat) - 50 records
-- ============================================================
INSERT INTO biz_metrics_stat (metric_name, category, metric_value, compare_value, change_rate, date, threshold_min, threshold_max, status, unit) VALUES
('日活跃用户数', '用户', 125800.00, 118500.00, 6.16, '2025-01-01', 100000.00, 200000.00, 1, '人'),
('日活跃用户数', '用户', 132000.00, 125800.00, 4.92, '2025-01-02', 100000.00, 200000.00, 1, '人'),
('日活跃用户数', '用户', 128500.00, 132000.00, -2.65, '2025-01-03', 100000.00, 200000.00, 1, '人'),
('日活跃用户数', '用户', 145000.00, 128500.00, 12.84, '2025-01-04', 100000.00, 200000.00, 1, '人'),
('日活跃用户数', '用户', 138000.00, 145000.00, -4.83, '2025-01-05', 100000.00, 200000.00, 1, '人'),
('订单总额', '交易', 2850000.00, 2650000.00, 7.55, '2025-01-01', 2000000.00, 5000000.00, 1, '元'),
('订单总额', '交易', 3120000.00, 2850000.00, 9.47, '2025-01-02', 2000000.00, 5000000.00, 1, '元'),
('订单总额', '交易', 2980000.00, 3120000.00, -4.49, '2025-01-03', 2000000.00, 5000000.00, 1, '元'),
('订单总额', '交易', 3450000.00, 2980000.00, 15.77, '2025-01-04', 2000000.00, 5000000.00, 1, '元'),
('订单总额', '交易', 3250000.00, 3450000.00, -5.80, '2025-01-05', 2000000.00, 5000000.00, 1, '元'),
('新增用户数', '用户', 2850.00, 3200.00, -10.94, '2025-01-01', 2000.00, 5000.00, 1, '人'),
('新增用户数', '用户', 3120.00, 2850.00, 9.47, '2025-01-02', 2000.00, 5000.00, 1, '人'),
('新增用户数', '用户', 2980.00, 3120.00, -4.49, '2025-01-03', 2000.00, 5000.00, 1, '人'),
('新增用户数', '用户', 3450.00, 2980.00, 15.77, '2025-01-04', 2000.00, 5000.00, 1, '人'),
('新增用户数', '用户', 3250.00, 3450.00, -5.80, '2025-01-05', 2000.00, 5000.00, 1, '人'),
('客单价', '交易', 168.50, 155.20, 8.57, '2025-01-01', 100.00, 300.00, 1, '元'),
('客单价', '交易', 175.80, 168.50, 4.33, '2025-01-02', 100.00, 300.00, 1, '元'),
('客单价', '交易', 172.30, 175.80, -1.99, '2025-01-03', 100.00, 300.00, 1, '元'),
('客单价', '交易', 185.60, 172.30, 7.72, '2025-01-04', 100.00, 300.00, 1, '元'),
('客单价', '交易', 180.20, 185.60, -2.91, '2025-01-05', 100.00, 300.00, 1, '元'),
('支付成功率', '交易', 98.52, 97.85, 0.68, '2025-01-01', 95.00, 100.00, 1, '%'),
('支付成功率', '交易', 98.78, 98.52, 0.26, '2025-01-02', 95.00, 100.00, 1, '%'),
('支付成功率', '交易', 98.45, 98.78, -0.33, '2025-01-03', 95.00, 100.00, 1, '%'),
('支付成功率', '交易', 98.92, 98.45, 0.48, '2025-01-04', 95.00, 100.00, 1, '%'),
('支付成功率', '交易', 98.65, 98.92, -0.27, '2025-01-05', 95.00, 100.00, 1, '%'),
('购物车转化率', '转化', 42.35, 40.25, 5.22, '2025-01-01', 30.00, 60.00, 1, '%'),
('购物车转化率', '转化', 44.18, 42.35, 4.32, '2025-01-02', 30.00, 60.00, 1, '%'),
('购物车转化率', '转化', 43.52, 44.18, -1.49, '2025-01-03', 30.00, 60.00, 1, '%'),
('购物车转化率', '转化', 45.85, 43.52, 5.35, '2025-01-04', 30.00, 60.00, 1, '%'),
('购物车转化率', '转化', 44.92, 45.85, -2.03, '2025-01-05', 30.00, 60.00, 1, '%'),
('退款率', '售后', 2.15, 2.35, -8.51, '2025-01-01', 0.00, 5.00, 1, '%'),
('退款率', '售后', 1.98, 2.15, -7.91, '2025-01-02', 0.00, 5.00, 1, '%'),
('退款率', '售后', 2.08, 1.98, 5.05, '2025-01-03', 0.00, 5.00, 1, '%'),
('退款率', '售后', 1.85, 2.08, -11.06, '2025-01-04', 0.00, 5.00, 1, '%'),
('退款率', '售后', 1.92, 1.85, 3.78, '2025-01-05', 0.00, 5.00, 1, '%'),
('库存周转率', '库存', 8.52, 8.25, 3.27, '2025-01-01', 5.00, 15.00, 1, '次'),
('库存周转率', '库存', 8.78, 8.52, 3.05, '2025-01-02', 5.00, 15.00, 1, '次'),
('库存周转率', '库存', 8.65, 8.78, -1.48, '2025-01-03', 5.00, 15.00, 1, '次'),
('库存周转率', '库存', 8.95, 8.65, 3.47, '2025-01-04', 5.00, 15.00, 1, '次'),
('库存周转率', '库存', 8.82, 8.95, -1.45, '2025-01-05', 5.00, 15.00, 1, '次'),
('商品曝光量', '流量', 5250000.00, 4980000.00, 5.42, '2025-01-01', 3000000.00, 10000000.00, 1, '次'),
('商品曝光量', '流量', 5480000.00, 5250000.00, 4.38, '2025-01-02', 3000000.00, 10000000.00, 1, '次'),
('商品曝光量', '流量', 5320000.00, 5480000.00, -2.92, '2025-01-03', 3000000.00, 10000000.00, 1, '次'),
('商品曝光量', '流量', 5680000.00, 5320000.00, 6.77, '2025-01-04', 3000000.00, 10000000.00, 1, '次'),
('商品曝光量', '流量', 5520000.00, 5680000.00, -2.82, '2025-01-05', 3000000.00, 10000000.00, 1, '次'),
('复购率', '用户', 35.25, 33.85, 4.13, '2025-01-01', 25.00, 50.00, 1, '%'),
('复购率', '用户', 36.52, 35.25, 3.60, '2025-01-02', 25.00, 50.00, 1, '%'),
('复购率', '用户', 35.98, 36.52, -1.48, '2025-01-03', 25.00, 50.00, 1, '%'),
('复购率', '用户', 37.25, 35.98, 3.53, '2025-01-04', 25.00, 50.00, 1, '%'),
('复购率', '用户', 36.85, 37.25, -1.07, '2025-01-05', 25.00, 50.00, 1, '%');
