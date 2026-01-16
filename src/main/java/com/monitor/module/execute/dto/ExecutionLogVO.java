package com.monitor.module.execute.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 执行日志结果VO
 *
 * @author monitor
 */
@Data
public class ExecutionLogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 执行时间
     */
    private Date execTime;

    /**
     * 执行状态：0-失败，1-成功
     */
    private Integer execStatus;

    /**
     * 触发类型：1-定时，2-手动，3-API调用
     */
    private Integer triggerType;

    /**
     * 执行耗时（毫秒）
     */
    private Integer durationMs;

    /**
     * 错误信息
     */
    private String errorMsg;
}
