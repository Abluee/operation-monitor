package com.monitor.module.execute.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 执行请求DTO
 *
 * @author monitor
 */
@Data
public class ExecuteRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 时间范围（格式：yyyy-MM-dd HH:mm:ss,yyyy-MM-dd HH:mm:ss）
     */
    private String timeRange;
}
