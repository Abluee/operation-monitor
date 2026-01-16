package com.monitor.module.task.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 阈值规则配置DTO
 *
 * @author monitor
 */
@Data
public class ThresholdRuleDTO {

    /**
     * 字段名
     */
    @NotBlank(message = "字段名不能为空")
    private String field;

    /**
     * 操作符：gt, lt, gte, lte, eq, ne
     */
    @NotBlank(message = "操作符不能为空")
    private String operator;

    /**
     * 阈值
     */
    @NotNull(message = "阈值不能为空")
    private Double threshold;

    /**
     * 级别：info, warning, error
     */
    @NotBlank(message = "级别不能为空")
    private String level;

    /**
     * 提示消息
     */
    @Size(max = 200, message = "提示消息长度不能超过200个字符")
    private String message;
}
