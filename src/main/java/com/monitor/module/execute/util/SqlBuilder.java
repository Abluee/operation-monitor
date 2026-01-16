package com.monitor.module.execute.util;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL构建工具类
 *
 * @author monitor
 */
public class SqlBuilder {

    /**
     * 时间占位符正则
     */
    private static final Pattern TIME_PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{timeRange\\}|\\$\\{startTime\\}|\\$\\{endTime\\}");

    /**
     * 构建最终执行的SQL
     *
     * @param typeSql    类型SQL
     * @param timeRange  时间范围（格式：yyyy-MM-dd HH:mm:ss,yyyy-MM-dd HH:mm:ss）
     * @param whereClause 额外的WHERE条件
     * @return 完整SQL
     */
    public static String buildFinalSql(String typeSql, String timeRange, String whereClause) {
        if (!StringUtils.hasText(typeSql)) {
            return null;
        }

        // 替换时间占位符
        String sql = replaceTimePlaceholder(typeSql, timeRange);

        // 追加额外的WHERE条件
        if (StringUtils.hasText(whereClause)) {
            sql = appendWhereClause(sql, whereClause);
        }

        return sql;
    }

    /**
     * 替换SQL中的时间占位符
     *
     * @param sql       SQL语句
     * @param timeRange 时间范围（格式：yyyy-MM-dd HH:mm:ss,yyyy-MM-dd HH:mm:ss）
     * @return 替换后的SQL
     */
    public static String replaceTimePlaceholder(String sql, String timeRange) {
        if (!StringUtils.hasText(sql)) {
            return sql;
        }

        Matcher matcher = TIME_PLACEHOLDER_PATTERN.matcher(sql);
        StringBuffer sb = new StringBuffer();

        if (StringUtils.hasText(timeRange) && timeRange.contains(",")) {
            String[] times = timeRange.split(",");
            String startTime = times[0].trim();
            String endTime = times.length > 1 ? times[1].trim() : times[0].trim();

            while (matcher.find()) {
                String placeholder = matcher.group();
                String replacement;
                if ("${timeRange}".equals(placeholder)) {
                    replacement = "'" + startTime + "' AND '" + endTime + "'";
                } else if ("${startTime}".equals(placeholder)) {
                    replacement = "'" + startTime + "'";
                } else if ("${endTime}".equals(placeholder)) {
                    replacement = "'" + endTime + "'";
                } else {
                    replacement = placeholder;
                }
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }
            matcher.appendTail(sb);
        } else {
            // 没有时间范围时，移除或保留占位符（根据业务需求）
            while (matcher.find()) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement("NULL"));
            }
            matcher.appendTail(sb);
        }

        return sb.toString();
    }

    /**
     * 构建子查询SQL
     *
     * @param sql SQL语句
     * @return 子查询SQL
     */
    public static String buildSubQuery(String sql) {
        if (!StringUtils.hasText(sql)) {
            return sql;
        }

        String trimmedSql = sql.trim();
        // 如果SQL已经包含子查询结构，直接返回
        if (trimmedSql.startsWith("(") && trimmedSql.endsWith(")")) {
            return sql;
        }

        // 如果SQL是SELECT语句，包装成子查询
        if (trimmedSql.toUpperCase().startsWith("SELECT")) {
            return "(" + sql + ")";
        }

        return sql;
    }

    /**
     * 追加WHERE条件
     *
     * @param sql        原始SQL
     * @param whereClause 追加的WHERE条件（不包含WHERE关键字）
     * @return 完整SQL
     */
    public static String appendWhereClause(String sql, String whereClause) {
        if (!StringUtils.hasText(sql)) {
            return sql;
        }

        String upperSql = sql.toUpperCase();

        // 查找WHERE关键字的位置
        int whereIndex = upperSql.indexOf("WHERE");
        if (whereIndex == -1) {
            // 没有WHERE子句，添加WHERE和条件
            return sql + " WHERE " + whereClause;
        } else {
            // 已有WHERE子句，使用AND追加条件
            int orderByIndex = upperSql.indexOf("ORDER BY");
            if (orderByIndex == -1) {
                // 没有ORDER BY，直接在WHERE后追加AND条件
                return sql.substring(0, whereIndex + 5) + " (" + sql.substring(whereIndex + 5) + ") AND (" + whereClause + ")";
            } else {
                // 有ORDER BY，在WHERE和ORDER BY之间追加AND条件
                return sql.substring(0, whereIndex + 5) + " (" + sql.substring(whereIndex + 5, orderByIndex) + ") AND (" + whereClause + ") " + sql.substring(orderByIndex);
            }
        }
    }

    /**
     * 验证SQL安全性（防止SQL注入）
     *
     * @param sql SQL语句
     * @return 是否安全
     */
    public static boolean validateSqlSecurity(String sql) {
        if (!StringUtils.hasText(sql)) {
            return true;
        }

        String upperSql = sql.toUpperCase();

        // 检查危险关键字
        String[] dangerousKeywords = {"DROP ", "DELETE ", "UPDATE ", "INSERT ", "ALTER ", "TRUNCATE ", "EXEC ", "EXECUTE "};
        for (String keyword : dangerousKeywords) {
            if (upperSql.contains(keyword)) {
                return false;
            }
        }

        // 检查分号（可能表示多条语句）
        if (upperSql.contains(";")) {
            return false;
        }

        return true;
    }
}
