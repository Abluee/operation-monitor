package com.monitor.module.execute.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 验证请求DTO
 *
 * @author monitor
 */
@Data
public class VerifyRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    private Long taskId;
}
