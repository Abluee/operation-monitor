package com.monitor.module.sqlparse.dto;

import java.util.List;

/**
 * SQL预览结果VO
 */
public class SqlPreviewResult {

    private List<String> columns;
    private List<List<Object>> rows;
    private long total;

    public SqlPreviewResult() {
    }

    public SqlPreviewResult(List<String> columns, List<List<Object>> rows, long total) {
        this.columns = columns;
        this.rows = rows;
        this.total = total;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<List<Object>> getRows() {
        return rows;
    }

    public void setRows(List<List<Object>> rows) {
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
