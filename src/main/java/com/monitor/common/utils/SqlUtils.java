package com.monitor.common.utils;

import org.springframework.util.StringUtils;

/**
 * SQL utility class
 */
public class SqlUtils {

    private SqlUtils() {
    }

    /**
     * Escape SQL string to prevent SQL injection
     */
    public static String escapeSql(String sql) {
        if (sql == null) {
            return null;
        }
        StringBuilder escaped = new StringBuilder();
        for (char c : sql.toCharArray()) {
            switch (c) {
                case '\'':
                    escaped.append("''");
                    break;
                case '"':
                    escaped.append("\"\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                case '\0':
                    escaped.append("\\0");
                    break;
                case '\b':
                    escaped.append("\\b");
                    break;
                case '\f':
                    escaped.append("\\f");
                    break;
                default:
                    escaped.append(c);
                    break;
            }
        }
        return escaped.toString();
    }

    /**
     * Validate SQL string for basic SQL injection patterns
     */
    public static boolean validateSql(String sql) {
        if (sql == null || sql.isEmpty()) {
            return false;
        }

        String lowerSql = sql.toLowerCase();

        // Check for dangerous keywords
        String[] dangerousKeywords = {
                "drop", "truncate", "delete", "update", "insert", "alter",
                "create", "execute", "exec", "union", "select", "grant",
                "revoke", "shutdown", "xp_", "sp_", "--", "/*", "*/",
                "information_schema", "sysdatabases", "sysobjects"
        };

        for (String keyword : dangerousKeywords) {
            if (lowerSql.contains(keyword)) {
                return false;
            }
        }

        // Check for SQL injection patterns
        String[] injectionPatterns = {
                "(\\s|\\')+(or|and)(\\s|\\')+",
                "(\\s|\\')+(\\w)+(\\s)*=(\\s)*(\\w)",
                "';.*--",
                ".*%27.*",
                ".*%20.*",
                "\\'.*OR.*=.*OR.*\\'",
                "\\'--",
                "\\'/*"
        };

        for (String pattern : injectionPatterns) {
            if (lowerSql.matches(".*" + pattern + ".*")) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validate that string contains only safe characters for SQL
     */
    public static boolean isSafeSqlString(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }

        // Allow only alphanumeric, spaces, and basic punctuation
        return str.matches("^[a-zA-Z0-9\\s_@\\-\\.]+$");
    }

    /**
     * Validate column name
     */
    public static boolean validateColumnName(String columnName) {
        if (columnName == null || columnName.isEmpty()) {
            return false;
        }
        // Column name should start with letter or underscore
        // and contain only letters, digits, underscores
        return columnName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$");
    }

    /**
     * Validate table name
     */
    public static boolean validateTableName(String tableName) {
        if (tableName == null || tableName.isEmpty()) {
            return false;
        }
        return tableName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$");
    }

    /**
     * Validate limit value
     */
    public static boolean validateLimit(Integer limit) {
        return limit != null && limit > 0 && limit <= 10000;
    }

    /**
     * Validate offset value
     */
    public static boolean validateOffset(Integer offset) {
        return offset != null && offset >= 0;
    }

    /**
     * Validate page number
     */
    public static boolean validatePageNum(Integer pageNum) {
        return pageNum != null && pageNum > 0;
    }

    /**
     * Validate page size
     */
    public static boolean validatePageSize(Integer pageSize) {
        return pageSize != null && pageSize > 0 && pageSize <= 100;
    }

    /**
     * Sanitize order by clause
     */
    public static String sanitizeOrderBy(String orderBy) {
        if (!StringUtils.hasText(orderBy)) {
            return null;
        }

        // Remove any semicolons or SQL comments
        String sanitized = orderBy.replace(";", "")
                .replace("--", "")
                .replace("/*", "")
                .replace("*/", "");

        // Allow only alphanumeric, underscore, comma, space, and ASC/DESC
        if (!sanitized.matches("^[a-zA-Z0-9_,\\s]+(ASC|DESC)?$")) {
            return null;
        }

        return sanitized;
    }

    /**
     * Build safe pagination SQL
     */
    public static String buildPaginationSql(String originalSql, int pageNum, int pageSize) {
        if (!StringUtils.hasText(originalSql)) {
            return originalSql;
        }

        int offset = (pageNum - 1) * pageSize;
        return originalSql + " LIMIT " + offset + ", " + pageSize;
    }

    /**
     * Check if SQL contains union
     */
    public static boolean containsUnion(String sql) {
        if (sql == null) {
            return false;
        }
        return sql.toLowerCase().contains("union");
    }

    /**
     * Check if SQL contains subquery
     */
    public static boolean containsSubquery(String sql) {
        if (sql == null) {
            return false;
        }
        return sql.contains("(") && sql.contains(")");
    }
}
