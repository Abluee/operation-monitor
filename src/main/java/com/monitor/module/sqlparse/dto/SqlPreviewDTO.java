package com.monitor.module.sqlparse.dto;

import javax.validation.constraints.NotBlank;

/**
 * SQL预览请求DTO
 */
public class SqlPreviewDTO {

    @NotBlank(message = "SQL语句不能为空")
    private String sql;

    private String timeRange = "1h";

    public SqlPreviewDTO() {
    }

    public SqlPreviewDTO(String sql, String timeRange) {
        this.sql = sql;
        this.timeRange = timeRange;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }
}
