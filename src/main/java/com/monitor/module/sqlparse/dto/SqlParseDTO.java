package com.monitor.module.sqlparse.dto;

import javax.validation.constraints.NotBlank;

/**
 * SQL解析请求DTO
 */
public class SqlParseDTO {

    @NotBlank(message = "SQL语句不能为空")
    private String sql;

    public SqlParseDTO() {
    }

    public SqlParseDTO(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
