package com.monitor.module.execute.util;

import com.monitor.module.execute.dto.ThresholdResult;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 阈值检查工具类
 *
 * @author monitor
 */
public class ThresholdChecker {

    /**
     * 检查数据是否满足阈值规则
     *
     * @param data  数据列表（Map格式）
     * @param rules 阈值规则列表
     * @return 阈值检查结果列表
     */
    public static List<ThresholdResult> check(List<Map<String, Object>> data, List<Map<String, Object>> rules) {
        List<ThresholdResult> results = new ArrayList<>();

        if (CollectionUtils.isEmpty(data) || CollectionUtils.isEmpty(rules)) {
            return results;
        }

        // 对每条数据进行检查
        for (Map<String, Object> record : data) {
            for (Map<String, Object> rule : rules) {
                ThresholdResult result = checkSingleRecord(record, rule);
                if (result != null) {
                    results.add(result);
                }
            }
        }

        return results;
    }

    /**
     * 检查单条数据是否满足阈值规则
     *
     * @param record 数据记录
     * @param rule   阈值规则
     * @return 阈值检查结果
     */
    public static ThresholdResult checkSingleRecord(Map<String, Object> record, Map<String, Object> rule) {
        if (record == null || rule == null) {
            return null;
        }

        String field = getStringValue(rule.get("field"));
        String operator = getStringValue(rule.get("operator"));
        Object threshold = rule.get("threshold");
        String level = getStringValue(rule.get("level"));
        String message = getStringValue(rule.get("message"));

        if (!StringUtils.hasText(field) || !StringUtils.hasText(operator)) {
            return null;
        }

        // 获取字段值
        Object fieldValue = getNestedValue(record, field);
        if (fieldValue == null) {
            return null;
        }

        // 检查是否触发阈值
        boolean triggered = checkCondition(fieldValue, operator, threshold);

        // 构建结果
        ThresholdResult result = new ThresholdResult();
        result.setField(field);
        result.setOperator(operator);
        result.setThreshold(threshold);
        result.setActualValue(fieldValue);
        result.setLevel(StringUtils.hasText(level) ? level : "warn");
        result.setTriggered(triggered);

        if (StringUtils.hasText(message)) {
            result.setMessage(message);
        } else {
            result.setMessage(buildDefaultMessage(field, operator, threshold, fieldValue, triggered));
        }

        return result;
    }

    /**
     * 检查条件是否满足
     *
     * @param value    实际值
     * @param operator 操作符
     * @param threshold 阈值
     * @return 是否满足条件
     */
    private static boolean checkCondition(Object value, String operator, Object threshold) {
        try {
            BigDecimal actual = toBigDecimal(value);
            BigDecimal thresh = toBigDecimal(threshold);

            if (actual == null || thresh == null) {
                return false;
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
     * 构建默认消息
     */
    private static String buildDefaultMessage(String field, String operator, Object threshold, Object actualValue, boolean triggered) {
        String status = triggered ? "触发" : "未触发";
        return String.format("字段[%s] %s 阈值[%s]，当前值[%s]，%s", field, operator, threshold, actualValue, status);
    }

    /**
     * 获取嵌套字段值
     *
     * @param record 数据记录
     * @param field  字段路径（支持点号分隔的嵌套字段）
     * @return 字段值
     */
    private static Object getNestedValue(Map<String, Object> record, String field) {
        if (record == null || !StringUtils.hasText(field)) {
            return null;
        }

        // 处理点号分隔的嵌套字段
        String[] parts = field.split("\\.");
        Object current = record;

        for (String part : parts) {
            if (current == null) {
                return null;
            }

            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
            } else {
                return null;
            }
        }

        return current;
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
}
