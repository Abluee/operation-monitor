package com.monitor.module.task.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 任务列表查询DTO
 *
 * @author monitor
 */
@Data
public class TaskListDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类型ID
     */
    private Long typeId;

    /**
     * 状态：0-待分配，1-方案待提交，2-方案待评审，3-优化中，4-待验收，5-已完成，null-全部
     */
    private Integer status;

    /**
     * 任务名称（模糊查询）
     */
    @Size(max = 100, message = "任务名称长度不能超过100个字符")
    private String taskName;

    /**
     * 任务负责人ID
     */
    private Long assigneeId;

    /**
     * 任务负责组ID
     */
    private Long groupId;

    /**
     * 过滤条件列表
     */
    private List<TaskFilterDTO> filter;

    /**
     * 页码，从1开始
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer pageSize = 10;
}
