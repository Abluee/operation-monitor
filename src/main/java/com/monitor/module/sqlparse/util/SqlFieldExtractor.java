package com.monitor.module.sqlparse.util;

import com.monitor.module.sqlparse.dto.SqlFieldDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL字段提取工具类
 */
@Component
public class SqlFieldExtractor {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{(\\w+)\\}\\}");
    private static final Pattern FIELD_PATTERN = Pattern.compile(
        "(?:\\b(?:AS\\s+)?([a-zA-Z_][a-zA-Z0-9_]*)(?:\\s+AS\\s+)?([a-zA-Z_][a-zA-Z0-9_]*))|" +
        "(?:([a-zA-Z_][a-zA-Z0-9_]*\\.[a-zA-Z_][a-zA-Z0-9_]*)" +
        "(?:\\s+AS\\s+)?([a-zA-Z_][a-zA-Z0-9_]*))"
    );

    /**
     * 解析SELECT语句中的字段
     *
     * @param sql SQL语句
     * @return 字段列表
     */
    public List<SqlFieldDTO> parseSelectFields(String sql) {
        List<SqlFieldDTO> fields = new ArrayList<>();

        if (sql == null || sql.trim().isEmpty()) {
            return fields;
        }

        String upperSql = sql.toUpperCase();
        int selectIndex = upperSql.indexOf("SELECT");
        int fromIndex = upperSql.indexOf("FROM", selectIndex);

        if (selectIndex == -1 || fromIndex == -1) {
            return fields;
        }

        String fieldsPart = sql.substring(selectIndex + 6, fromIndex).trim();
        String[] fieldTokens = splitFields(fieldsPart);

        for (String field : fieldTokens) {
            field = field.trim();
            if (field.isEmpty() || field.equals("*")) {
                fields.add(new SqlFieldDTO("*", "ALL", "string"));
                continue;
            }

            SqlFieldDTO fieldDTO = parseField(field);
            if (fieldDTO != null) {
                fields.add(fieldDTO);
            }
        }

        return fields;
    }

    /**
     * 识别SQL中的占位符
     *
     * @param sql SQL语句
     * @return 占位符列表
     */
    public List<String> identifyPlaceholders(String sql) {
        List<String> placeholders = new ArrayList<>();

        if (sql == null) {
            return placeholders;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(sql);
        while (matcher.find()) {
            String placeholder = matcher.group(1);
            if (!placeholders.contains(placeholder)) {
                placeholders.add(placeholder);
            }
        }

        return placeholders;
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

    private SqlFieldDTO parseField(String field) {
        String trimmed = field.trim();
        String alias = null;
        String columnName = trimmed;

        int asIndex = findAsIndex(trimmed);
        if (asIndex != -1) {
            columnName = trimmed.substring(0, asIndex).trim();
            alias = trimmed.substring(asIndex + 2).trim();
        }

        String type = inferFieldType(columnName);
        boolean nullable = true;
        Integer length = null;
        Integer precision = null;
        Integer scale = null;

        if (columnName.contains("COUNT(") || columnName.contains("SUM(") ||
            columnName.contains("AVG(") || columnName.contains("MAX(") ||
            columnName.contains("MIN(")) {
            type = "NUMERIC";
            precision = 20;
            scale = 4;
        } else if (columnName.toUpperCase().contains("VARCHAR") ||
                   columnName.toUpperCase().contains("CHAR")) {
            length = 255;
            type = "STRING";
        } else if (columnName.toUpperCase().contains("INT") ||
                   columnName.toUpperCase().contains("INTEGER")) {
            type = "INTEGER";
        } else if (columnName.toUpperCase().contains("DECIMAL") ||
                   columnName.toUpperCase().contains("NUMERIC")) {
            precision = 18;
            scale = 4;
            type = "DECIMAL";
        } else if (columnName.toUpperCase().contains("TIMESTAMP") ||
                   columnName.toUpperCase().contains("DATETIME")) {
            type = "TIMESTAMP";
        } else if (columnName.toUpperCase().contains("DATE")) {
            type = "DATE";
        } else if (columnName.toUpperCase().contains("TIME")) {
            type = "TIME";
        } else if (columnName.toUpperCase().contains("BOOLEAN") ||
                   columnName.toUpperCase().contains("BOOL")) {
            type = "BOOLEAN";
        }

        String displayName = alias != null ? alias : extractFieldName(columnName);

        return new SqlFieldDTO(columnName, displayName, mapToDataType(type));
    }

    private String mapToDataType(String sqlType) {
        if (sqlType == null) {
            return "string";
        }
        String upper = sqlType.toUpperCase();
        if (upper.contains("NUMERIC") || upper.contains("DECIMAL") ||
            upper.contains("INTEGER") || upper.contains("INT") ||
            upper.contains("DOUBLE") || upper.contains("FLOAT") ||
            upper.contains("REAL") || upper.contains("BIGINT") ||
            upper.contains("SMALLINT") || upper.contains("TINYINT")) {
            return "number";
        }
        if (upper.contains("DATE") || upper.contains("TIME") ||
            upper.contains("TIMESTAMP") || upper.contains("DATETIME")) {
            return "date";
        }
        return "string";
    }

    private int findAsIndex(String str) {
        String upper = str.toUpperCase();
        int asIndex = -1;
        int searchFrom = 0;

        while (true) {
            int pos = upper.indexOf(" AS ", searchFrom);
            if (pos == -1) {
                int simpleAs = upper.indexOf(" AS", searchFrom);
                if (simpleAs != -1 && (simpleAs + 4 >= str.length() ||
                    Character.isWhitespace(str.charAt(simpleAs + 3)))) {
                    asIndex = simpleAs;
                }
                break;
            }
            asIndex = pos;
            searchFrom = pos + 4;
        }

        return asIndex;
    }

    private String extractFieldName(String columnName) {
        if (columnName.contains(".")) {
            return columnName.substring(columnName.lastIndexOf(".") + 1);
        }
        return columnName;
    }

    private String inferFieldType(String columnName) {
        String upper = columnName.toUpperCase();

        if (upper.contains("COUNT(") || upper.contains("SUM(") ||
            upper.contains("AVG(") || upper.contains("MAX(") || upper.contains("MIN(")) {
            return "NUMERIC";
        }
        if (upper.contains("DISTINCT ")) {
            return "DISTINCT";
        }
        if (upper.contains("CASE ")) {
            return "CONDITIONAL";
        }

        return "UNKNOWN";
    }
}
