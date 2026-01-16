package com.monitor.module.sqlparse.dto;

/**
 * SQL字段DTO
 */
public class SqlFieldDTO {

    private String sqlField;
    private String name;
    private String dataType;

    public SqlFieldDTO() {
    }

    public SqlFieldDTO(String sqlField, String name, String dataType) {
        this.sqlField = sqlField;
        this.name = name;
        this.dataType = dataType;
    }

    public String getSqlField() {
        return sqlField;
    }

    public void setSqlField(String sqlField) {
        this.sqlField = sqlField;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
