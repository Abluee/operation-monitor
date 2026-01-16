package com.monitor.module.task.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 任务手动执行DTO
 *
 * @author monitor
 */
@Data
public class TaskExecuteDTO {

    /**
     * 任务ID
     */
    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    /**
     * 时间范围（可选），格式：yyyy-MM-dd HH:mm:ss,yyyy-MM-dd HH:mm:ss
     */
    @Size(max = 50, message = "时间范围格式不正确")
    private String timeRange;
}
