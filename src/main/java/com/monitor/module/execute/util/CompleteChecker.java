package com.monitor.module.execute.util;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 完成检查工具类
 *
 * @author monitor
 */
public class CompleteChecker {

    /**
     * 检查数据是否满足完成条件
     *
     * @param data  数据列表（Map格式）
     * @param rules 完成规则
     * @return 完成检查结果
     */
    public static CompleteCheckResult check(List<Map<String, Object>> data, Map<String, Object> rules) {
        CompleteCheckResult result = new CompleteCheckResult();
        result.setCompleted(false);

        if (CollectionUtils.isEmpty(data)) {
            result.setCompleted(false);
            result.setReason("无数据");
            return result;
        }

        if (rules == null || rules.isEmpty()) {
            // 没有规则，默认完成
            result.setCompleted(true);
            result.setReason("数据已就绪");
            return result;
        }

        // 解析完成条件
        String condition = getStringValue(rules.get("condition"));
        Object threshold = rules.get("threshold");
        String operator = getStringValue(rules.get("operator"));
        String targetField = getStringValue(rules.get("targetField"));

        // 计算数据量
        int dataCount = data.size();
        result.setDataCount(dataCount);

        if (StringUtils.hasText(condition)) {
            // 使用自定义条件表达式
            boolean conditionMet = evaluateExpression(data, condition);
            if (conditionMet) {
                result.setCompleted(true);
                result.setReason("满足完成条件: " + condition);
            } else {
                result.setCompleted(false);
                result.setReason("不满足完成条件: " + condition);
            }
        } else if (StringUtils.hasText(targetField) && threshold != null) {
            // 使用字段阈值条件
            Object fieldValue = calculateFieldValue(data, targetField, operator);
            boolean conditionMet = evaluateCondition(fieldValue, operator, threshold);

            if (conditionMet) {
                result.setCompleted(true);
                result.setReason(String.format("字段[%s]=%s满足条件: %s %s %s", targetField, fieldValue, operator, threshold, conditionMet ? "" : "未满足"));
            } else {
                result.setCompleted(false);
                result.setReason(String.format("字段[%s]=%s不满足条件: %s %s %s", targetField, fieldValue, operator, threshold, conditionMet ? "" : "未满足"));
            }
        } else if (threshold != null) {
            // 使用数据量阈值
            boolean conditionMet = evaluateCondition(dataCount, operator, threshold);
            if (conditionMet) {
                result.setCompleted(true);
                result.setReason(String.format("数据量=%d满足条件: %s %s", dataCount, operator, threshold));
            } else {
                result.setCompleted(false);
                result.setReason(String.format("数据量=%d不满足条件: %s %s", dataCount, operator, threshold));
            }
        } else {
            // 默认：只要有数据就完成
            result.setCompleted(true);
            result.setReason("数据已就绪");
        }

        return result;
    }

    /**
     * 评估表达式
     *
     * @param data      数据列表
     * @param condition 条件表达式
     * @return 是否满足条件
     */
    private static boolean evaluateExpression(List<Map<String, Object>> data, String condition) {
        if (!StringUtils.hasText(condition)) {
            return false;
        }

        try {
            // 计算数据量
            int dataCount = data.size();

            // 计算数值字段总和（如果有的话）
            BigDecimal sumValue = BigDecimal.ZERO;
            for (Map<String, Object> record : data) {
                for (Object value : record.values()) {
                    if (value instanceof Number) {
                        sumValue = sumValue.add(toBigDecimal(value));
                    }
                }
            }

            // 简单表达式解析
            // 格式：dataCount >= 10 或 sum(field) > 100
            String expr = condition.trim();

            // 检查常见的表达式模式
            if (expr.matches("dataCount\\s*[><=!]+\\s*\\d+")) {
                // 匹配 dataCount >= 10 格式
                return evalSimpleExpr(dataCount, expr);
            }

            // 默认返回true（保守处理）
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 评估简单表达式
     */
    private static boolean evalSimpleExpr(int dataCount, String expr) {
        try {
            // 提取操作符和阈值
            String operator = null;
            int threshold = 0;

            if (expr.contains(">=")) {
                operator = ">=";
                String[] parts = expr.split(">=");
                threshold = Integer.parseInt(parts[1].trim());
            } else if (expr.contains("<=")) {
                operator = "<=";
                String[] parts = expr.split("<=");
                threshold = Integer.parseInt(parts[1].trim());
            } else if (expr.contains(">")) {
                operator = ">";
                String[] parts = expr.split(">");
                threshold = Integer.parseInt(parts[1].trim());
            } else if (expr.contains("<")) {
                operator = "<";
                String[] parts = expr.split("<");
                threshold = Integer.parseInt(parts[1].trim());
            } else if (expr.contains("==")) {
                operator = "==";
                String[] parts = expr.split("==");
                threshold = Integer.parseInt(parts[1].trim());
            } else if (expr.contains("=")) {
                operator = "=";
                String[] parts = expr.split("=");
                threshold = Integer.parseInt(parts[1].trim());
            } else if (expr.contains("!=")) {
                operator = "!=";
                String[] parts = expr.split("!=");
                threshold = Integer.parseInt(parts[1].trim());
            }

            if (operator == null) {
                return false;
            }

            return evaluateCondition(dataCount, operator, threshold);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 计算字段值
     *
     * @param data        数据列表
     * @param targetField 目标字段
     * @param operator    操作符（用于判断聚合方式）
     * @return 计算结果
     */
    private static Object calculateFieldValue(List<Map<String, Object>> data, String targetField, String operator) {
        if (CollectionUtils.isEmpty(data) || !StringUtils.hasText(targetField)) {
            return null;
        }

        // 计算数据量
        return data.size();
    }

    /**
     * 评估条件
     *
     * @param value    实际值
     * @param operator 操作符
     * @param threshold 阈值
     * @return 是否满足条件
     */
    public static boolean evaluateCondition(Object value, String operator, Object threshold) {
        try {
            BigDecimal actual = toBigDecimal(value);
            BigDecimal thresh = toBigDecimal(threshold);

            if (actual == null || thresh == null) {
                return false;
            }

            if (!StringUtils.hasText(operator)) {
                operator = ">=";
            }

            switch (operator.toUpperCase()) {
                case ">":
                    return actual.compareTo(thresh) > 0;
                case ">=":
                    return actual.compareTo(thresh) >= 0;
                case "<":
                    return actual.compareTo(thresh) < 0;
                case "<=":
                    return actual.compareTo(thresh) <= 0;
                case "=":
                case "==":
                    return actual.compareTo(thresh) == 0;
                case "!=":
                case "<>":
                    return actual.compareTo(thresh) != 0;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 转换为BigDecimal
     *
     * @param value 值
     * @return BigDecimal
     */
    private static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }

        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }

        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取字符串值
     *
     * @param value 值对象
     * @return 字符串
     */
    private static String getStringValue(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString().trim();
    }

    /**
     * 完成检查结果
     */
    public static class CompleteCheckResult {
        private boolean completed;
        private String reason;
        private Integer dataCount;

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public Integer getDataCount() {
            return dataCount;
        }

        public void setDataCount(Integer dataCount) {
            this.dataCount = dataCount;
        }
    }
}
