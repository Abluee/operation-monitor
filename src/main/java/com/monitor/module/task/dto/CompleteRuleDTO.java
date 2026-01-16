package com.monitor.module.task.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

/**
 * 完成规则配置DTO
 *
 * @author monitor
 */
@Data
public class CompleteRuleDTO {

    /**
     * 条件类型：all, any, custom
     */
    @NotBlank(message = "条件类型不能为空")
    private String conditionType;

    /**
     * 规则列表
     */
    private List<Map<String, Object>> rules;

    /**
     * 完成时是否通知
     */
    private Boolean notifyOnComplete;

    /**
     * 完成消息
     */
    @Size(max = 500, message = "完成消息长度不能超过500个字符")
    private String completeMessage;
}
