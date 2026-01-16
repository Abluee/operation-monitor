package com.monitor.module.notify.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Notification request DTO
 *
 * @author monitor
 */
@Data
public class NotifyRequest {

    /**
     * Task ID
     */
    private Long taskId;

    /**
     * Task name
     */
    private String taskName;

    /**
     * Threshold violations list
     */
    private List<Map<String, Object>> thresholdViolations;

    /**
     * Complete reason
     */
    private String completeReason;

    /**
     * Data summary (JSON format)
     */
    private Map<String, Object> dataSummary;

    /**
     * Notification time
     */
    private Date notifyTime;

    /**
     * Notification channel (optional, use configured channels if not specified)
     */
    private String channel;

    /**
     * Whether to record notification history
     */
    private Boolean recordHistory = true;
}
