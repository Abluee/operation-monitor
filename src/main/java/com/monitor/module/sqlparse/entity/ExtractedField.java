package com.monitor.module.sqlparse.entity;

/**
 * SQL字段提取结果实体
 */
public class ExtractedField {

    private String name;
    private String type;
    private boolean nullable;
    private Integer length;
    private Integer precision;
    private Integer scale;

    public ExtractedField() {
    }

    public ExtractedField(String name, String type, boolean nullable, Integer length, Integer precision, Integer scale) {
        this.name = name;
        this.type = type;
        this.nullable = nullable;
        this.length = length;
        this.precision = precision;
        this.scale = scale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }
}
