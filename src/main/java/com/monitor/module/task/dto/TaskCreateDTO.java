package com.monitor.module.task.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

/**
 * 任务创建DTO
 *
 * @author monitor
 */
@Data
public class TaskCreateDTO {

    /**
     * 类型ID
     */
    @NotNull(message = "类型ID不能为空")
    private Long typeId;

    /**
     * 任务名称
     */
    @NotBlank(message = "任务名称不能为空")
    @Size(max = 100, message = "任务名称长度不能超过100个字符")
    private String taskName;

    /**
     * 任务描述
     */
    @Size(max = 500, message = "任务描述长度不能超过500个字符")
    private String description;

    /**
     * 阈值规则配置
     */
    private Map<String, Object> thresholdRules;

    /**
     * 完成规则配置
     */
    private Map<String, Object> completeRules;

    /**
     * 查询条件配置
     */
    private Map<String, Object> queryCondition;

    /**
     * 计算公式
     */
    @Size(max = 500, message = "计算公式长度不能超过500个字符")
    private String formula;

    /**
     * 任务负责人ID
     */
    private Long assigneeId;

    /**
     * 任务负责组ID
     */
    private Long groupId;
}
