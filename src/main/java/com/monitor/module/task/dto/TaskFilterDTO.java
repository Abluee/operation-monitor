package com.monitor.module.task.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.io.Serializable;

/**
 * 任务过滤条件DTO
 * 支持两种格式：
 * 1. {"field": "total", "value": [1, null]} - 标准格式
 * 2. {"total": [1, null]} - 简化格式（字段名直接作为key）
 *
 * @author monitor
 */
@Data
public class TaskFilterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 过滤字段：api, date, total, slowAvg, slowRate, slowCount, totalAvg
     */
    private String field;

    /**
     * 过滤值：
     * - 字符串类型字段（api）：直接匹配，支持模糊查询
     * - 范围类型字段：数组 [min, max]，null表示不限制
     */
    private Object value;

    /**
     * 支持简化格式 {"total": [1, null]}，字段名直接作为key
     * Jackson会调用此方法处理未知的JSON属性
     */
    @JsonAnySetter
    public void setDynamicField(String fieldName, Object fieldValue) {
        this.field = fieldName;
        this.value = fieldValue;
    }
}
