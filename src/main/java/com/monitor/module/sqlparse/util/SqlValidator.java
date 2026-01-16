package com.monitor.module.sqlparse.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * SQL验证工具类
 */
@Component
public class SqlValidator {

    private static final Pattern SELECT_PATTERN = Pattern.compile(
        "^\\s*SELECT\\s+.*FROM\\s+.*",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private static final Pattern BASIC_SELECT_PATTERN = Pattern.compile(
        "^\\s*SELECT\\s+",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern FROM_PATTERN = Pattern.compile(
        "\\s+FROM\\s+",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern SEMICOLON_PATTERN = Pattern.compile(";");
    private static final Pattern DANGEROUS_KEYWORDS = Pattern.compile(
        "(?i)\\b(DROP|DELETE|TRUNCATE|INSERT|UPDATE|CREATE|ALTER|EXEC|EXECUTE)\\b"
    );

    /**
     * 验证基本SQL语法
     *
     * @param sql SQL语句
     * @return 是否有效
     */
    public boolean validateBasicSyntax(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }

        String trimmedSql = sql.trim();

        if (SEMICOLON_PATTERN.matcher(trimmedSql).find()) {
            return false;
        }

        if (DANGEROUS_KEYWORDS.matcher(trimmedSql).find()) {
            return false;
        }

        if (!BASIC_SELECT_PATTERN.matcher(trimmedSql).find()) {
            return false;
        }

        return true;
    }

    /**
     * 验证SELECT语句
     *
     * @param sql SQL语句
     * @return 是否为有效的SELECT语句
     */
    public boolean validateSelectStatement(String sql) {
        if (!validateBasicSyntax(sql)) {
            return false;
        }

        String upperSql = sql.toUpperCase();
        int selectIndex = upperSql.indexOf("SELECT");
        int fromIndex = upperSql.indexOf("FROM", selectIndex);

        if (selectIndex == -1 || fromIndex == -1) {
            return false;
        }

        String fieldsPart = sql.substring(selectIndex + 6, fromIndex).trim();
        if (fieldsPart.isEmpty()) {
            return false;
        }

        String afterFrom = sql.substring(fromIndex + 4).trim();
        if (afterFrom.isEmpty()) {
            return false;
        }

        return SELECT_PATTERN.matcher(sql).matches();
    }

    /**
     * 检查SQL是否只读
     *
     * @param sql SQL语句
     * @return 是否只读
     */
    public boolean isReadOnly(String sql) {
        if (sql == null) {
            return false;
        }

        String upperSql = sql.toUpperCase();
        return upperSql.startsWith("SELECT") &&
               !DANGEROUS_KEYWORDS.matcher(upperSql).find();
    }
}
