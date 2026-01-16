package com.monitor.module.execute.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 阈值检查结果DTO
 *
 * @author monitor
 */
@Data
public class ThresholdResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字段名
     */
    private String field;

    /**
     * 操作符：>, >=, <, <=, ==, !=
     */
    private String operator;

    /**
     * 阈值
     */
    private Object threshold;

    /**
     * 实际值
     */
    private Object actualValue;

    /**
     * 级别：warn, error
     */
    private String level;

    /**
     * 消息
     */
    private String message;

    /**
     * 是否触发
     */
    private Boolean triggered;
}
