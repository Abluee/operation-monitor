package com.monitor.module.execute.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 验证结果VO
 *
 * @author monitor
 */
@Data
public class VerifyResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 验证值
     */
    private Object value;

    /**
     * 详细数据列表
     */
    private List<Object> detail;

    /**
     * 是否完成
     */
    private Boolean isCompleted;

    /**
     * 阈值检查结果列表
     */
    private List<ThresholdResult> thresholdResults;

    /**
     * 完成原因
     */
    private String completedReason;
}
