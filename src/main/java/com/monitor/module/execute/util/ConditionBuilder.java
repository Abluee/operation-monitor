package com.monitor.module.execute.util;

import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 条件构建工具类
 *
 * @author monitor
 */
public class ConditionBuilder {

    /**
     * 构建WHERE条件语句
     *
     * @param condition 查询条件对象（Map格式）
     * @return WHERE条件SQL片段
     */
    public static String buildWhereClause(Map<String, Object> condition) {
        if (condition == null || condition.isEmpty()) {
            return "";
        }

        StringBuilder whereClause = new StringBuilder();

        for (Map.Entry<String, Object> entry : condition.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();

            if (whereClause.length() > 0) {
                whereClause.append(" AND ");
            }

            if (value == null) {
                whereClause.append(field).append(" IS NULL");
            } else if (value instanceof List) {
                // 处理IN条件
                whereClause.append(field).append(" IN (").append(formatListValue((List<?>) value)).append(")");
            } else if (value instanceof Map) {
                // 处理复杂条件
                whereClause.append(buildCondition(field, (Map<String, Object>) value));
            } else {
                // 处理简单等值条件
                whereClause.append(field).append(" = ").append(formatValue(value));
            }
        }

        return whereClause.toString();
    }

    /**
     * 构建单个条件
     *
     * @param field  字段名
     * @param item   条件项（Map格式，包含operator和value）
     * @return 条件SQL片段
     */
    public static String buildCondition(String field, Map<String, Object> item) {
        if (item == null || !item.containsKey("operator") || !item.containsKey("value")) {
            return field + " = " + formatValue(item.get("value"));
        }

        String operator = getStringValue(item.get("operator"));
        Object value = item.get("value");

        return buildFieldCondition(field, operator, value);
    }

    /**
     * 构建字段条件
     *
     * @param field    字段名
     * @param operator 操作符
     * @param value    值
     * @return 条件SQL片段
     */
    public static String buildFieldCondition(String field, String operator, Object value) {
        if (!StringUtils.hasText(operator)) {
            operator = "=";
        }

        String formattedValue = formatValue(value);

        switch (operator.toUpperCase()) {
            case "=":
            case "==":
                return field + " = " + formattedValue;
            case "!=":
            case "<>":
                return field + " != " + formattedValue;
            case ">":
                return field + " > " + formattedValue;
            case ">=":
                return field + " >= " + formattedValue;
            case "<":
                return field + " < " + formattedValue;
            case "<=":
                return field + " <= " + formattedValue;
            case "LIKE":
                return field + " LIKE " + formattedValue;
            case "NOT LIKE":
                return field + " NOT LIKE " + formattedValue;
            case "IN":
                if (value instanceof List) {
                    return field + " IN (" + formatListValue((List<?>) value) + ")";
                }
                return field + " IN (" + formattedValue + ")";
            case "NOT IN":
                if (value instanceof List) {
                    return field + " NOT IN (" + formatListValue((List<?>) value) + ")";
                }
                return field + " NOT IN (" + formattedValue + ")";
            case "BETWEEN":
                if (value instanceof List && ((List<?>) value).size() >= 2) {
                    List<?> list = (List<?>) value;
                    return field + " BETWEEN " + formatValue(list.get(0)) + " AND " + formatValue(list.get(1));
                }
                return field + " = " + formattedValue;
            case "IS NULL":
                return field + " IS NULL";
            case "IS NOT NULL":
                return field + " IS NOT NULL";
            default:
                return field + " = " + formattedValue;
        }
    }

    /**
     * 格式化值为SQL字符串
     *
     * @param value 值
     * @return 格式化后的SQL值
     */
    private static String formatValue(Object value) {
        if (value == null) {
            return "NULL";
        }

        if (value instanceof Number) {
            return value.toString();
        }

        if (value instanceof Boolean) {
            return (Boolean) value ? "1" : "0";
        }

        // 字符串需要转义
        String strValue = value.toString();
        strValue = strValue.replace("'", "''");
        strValue = strValue.replace("\\", "\\\\");
        return "'" + strValue + "'";
    }

    /**
     * 格式化列表值为SQL字符串
     *
     * @param list 列表
     * @return 格式化后的SQL值列表
     */
    private static String formatListValue(List<?> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(formatValue(list.get(i)));
        }
        return sb.toString();
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
