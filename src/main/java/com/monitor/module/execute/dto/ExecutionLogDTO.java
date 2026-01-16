package com.monitor.module.execute.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 执行日志查询DTO
 *
 * @author monitor
 */
@Data
public class ExecutionLogDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 执行状态
     */
    private Integer execStatus;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;
}
