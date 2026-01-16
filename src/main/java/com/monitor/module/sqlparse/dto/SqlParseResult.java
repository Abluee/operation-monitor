package com.monitor.module.sqlparse.dto;

import java.util.List;

/**
 * SQL解析结果VO
 */
public class SqlParseResult {

    private List<SqlFieldDTO> fields;
    private List<String> placeholders;
    private String sqlType;

    public SqlParseResult() {
    }

    public SqlParseResult(List<SqlFieldDTO> fields, List<String> placeholders, String sqlType) {
        this.fields = fields;
        this.placeholders = placeholders;
        this.sqlType = sqlType;
    }

    public List<SqlFieldDTO> getFields() {
        return fields;
    }

    public void setFields(List<SqlFieldDTO> fields) {
        this.fields = fields;
    }

    public List<String> getPlaceholders() {
        return placeholders;
    }

    public void setPlaceholders(List<String> placeholders) {
        this.placeholders = placeholders;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }
}
