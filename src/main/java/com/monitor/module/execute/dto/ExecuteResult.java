package com.monitor.module.execute.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 执行结果VO
 *
 * @author monitor
 */
@Data
public class ExecuteResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 执行时间
     */
    private Date execTime;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 执行耗时（毫秒）
     */
    private Long durationMs;

    /**
     * 数据条数
     */
    private Integer dataCount;

    /**
     * 阈值违规数量
     */
    private Integer thresholdViolations;

    /**
     * 是否完成
     */
    private Boolean isCompleted;

    /**
     * 完成原因
     */
    private String completedReason;

    /**
     * 消息
     */
    private String message;
}
