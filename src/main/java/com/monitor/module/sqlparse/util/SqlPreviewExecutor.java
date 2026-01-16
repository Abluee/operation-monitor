package com.monitor.module.sqlparse.util;

import com.monitor.module.sqlparse.dto.SqlFieldDTO;
import com.monitor.module.sqlparse.dto.SqlPreviewResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * SQL预览执行工具类
 */
@Component
public class SqlPreviewExecutor {

    /**
     * 执行SQL预览
     *
     * @param sql       SQL语句
     * @param timeRange 时间范围
     * @param limit     结果限制
     * @return 预览结果
     */
    public SqlPreviewResult executePreview(String sql, String timeRange, int limit) {
        List<String> columns = extractColumns(sql);
        List<List<Object>> rows = generatePreviewData(columns, timeRange, limit);
        long total = rows.size();

        return new SqlPreviewResult(columns, rows, total);
    }

    /**
     * 从SQL中提取列名
     *
     * @param sql SQL语句
     * @return 列名列表
     */
    private List<String> extractColumns(String sql) {
        List<String> columns = new ArrayList<>();

        String upperSql = sql.toUpperCase();
        int selectIndex = upperSql.indexOf("SELECT");
        int fromIndex = upperSql.indexOf("FROM", selectIndex);

        if (selectIndex == -1 || fromIndex == -1) {
            return columns;
        }

        String fieldsPart = sql.substring(selectIndex + 6, fromIndex).trim();
        if (fieldsPart.equals("*")) {
            columns.add("column_1");
            columns.add("column_2");
            columns.add("column_3");
            return columns;
        }

        String[] fieldTokens = splitFields(fieldsPart);
        for (String field : fieldTokens) {
            String trimmed = field.trim();
            String columnName = extractFieldName(trimmed);
            columns.add(columnName);
        }

        return columns;
    }

    /**
     * 生成预览数据
     *
     * @param columns   列名列表
     * @param timeRange 时间范围
     * @param limit     行数限制
     * @return 预览数据
     */
    private List<List<Object>> generatePreviewData(List<String> columns, String timeRange, int limit) {
        List<List<Object>> rows = new ArrayList<>();

        int rowCount = Math.min(limit, 10);

        for (int i = 0; i < rowCount; i++) {
            List<Object> row = new ArrayList<>();
            for (int j = 0; j < columns.size(); j++) {
                row.add(generateSampleValue(columns.get(j), j, i));
            }
            rows.add(row);
        }

        return rows;
    }

    /**
     * 生成示例值
     *
     * @param columnName 列名
     * @param columnIndex 列索引
     * @param rowIndex 行索引
     * @return 示例值
     */
    private Object generateSampleValue(String columnName, int columnIndex, int rowIndex) {
        String upperName = columnName.toUpperCase();

        if (upperName.contains("ID") || upperName.contains("_ID")) {
            return UUID.randomUUID().toString().substring(0, 8) + "-" + rowIndex;
        }
        if (upperName.contains("NAME") || upperName.contains("_NAME")) {
            return "Sample_" + (rowIndex + 1);
        }
        if (upperName.contains("STATUS") || upperName.contains("STATE")) {
            String[] statuses = {"ACTIVE", "INACTIVE", "PENDING"};
            return statuses[rowIndex % statuses.length];
        }
        if (upperName.contains("COUNT") || upperName.contains("NUM") ||
            upperName.contains("AMOUNT") || upperName.contains("QTY")) {
            return (rowIndex + 1) * 10;
        }
        if (upperName.contains("PRICE") || upperName.contains("RATE")) {
            return (rowIndex + 1) * 9.99;
        }
        if (upperName.contains("DATE") || upperName.contains("TIME") ||
            upperName.contains("CREATED") || upperName.contains("UPDATED")) {
            return "2026-01-08 10:30:" + String.format("%02d", rowIndex * 5);
        }
        if (upperName.contains("EMAIL")) {
            return "user" + (rowIndex + 1) + "@example.com";
        }
        if (upperName.contains("BOOL") || upperName.contains("FLAG") ||
            upperName.contains("IS_")) {
            return rowIndex % 2 == 0;
        }
        if (columnName.startsWith("column_")) {
            return "value_" + (rowIndex + 1);
        }

        return "sample_" + (rowIndex + 1);
    }

    private String[] splitFields(String fieldsPart) {
        List<String> fields = new ArrayList<>();
        int parenDepth = 0;
        int start = 0;

        for (int i = 0; i < fieldsPart.length(); i++) {
            char c = fieldsPart.charAt(i);
            if (c == '(') {
                parenDepth++;
            } else if (c == ')') {
                parenDepth--;
            } else if (c == ',' && parenDepth == 0) {
                fields.add(fieldsPart.substring(start, i));
                start = i + 1;
            }
        }

        fields.add(fieldsPart.substring(start));
        return fields.toArray(new String[0]);
    }

    private String extractFieldName(String field) {
        String trimmed = field.trim();

        int asIndex = trimmed.toUpperCase().indexOf(" AS ");
        if (asIndex != -1) {
            return trimmed.substring(asIndex + 4).trim();
        }

        if (trimmed.contains(".")) {
            String lastPart = trimmed.substring(trimmed.lastIndexOf(".") + 1);
            asIndex = lastPart.toUpperCase().indexOf(" AS ");
            if (asIndex != -1) {
                return lastPart.substring(asIndex + 4).trim();
            }
            return lastPart;
        }

        asIndex = trimmed.toUpperCase().indexOf(" AS");
        if (asIndex != -1) {
            return trimmed.substring(asIndex + 3).trim();
        }

        return trimmed;
    }
}
