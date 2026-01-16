package com.monitor.module.notify.dto;

import lombok.Data;

import java.util.Date;

/**
 * Notification result VO
 *
 * @author monitor
 */
@Data
public class NotifyResult {

    /**
     * Whether the notification was sent successfully
     */
    private Boolean success;

    /**
     * Notification channel
     */
    private String channel;

    /**
     * Result message
     */
    private String message;

    /**
     * Send time
     */
    private Date sentTime;

    /**
     * Error message (if failed)
     */
    private String errorMsg;

    /**
     * Record ID
     */
    private Long recordId;

    /**
     * Create a success result
     */
    public static NotifyResult success(String channel, String message) {
        NotifyResult result = new NotifyResult();
        result.setSuccess(true);
        result.setChannel(channel);
        result.setMessage(message);
        result.setSentTime(new Date());
        return result;
    }

    /**
     * Create a failure result
     */
    public static NotifyResult fail(String channel, String errorMsg) {
        NotifyResult result = new NotifyResult();
        result.setSuccess(false);
        result.setChannel(channel);
        result.setMessage("failed");
        result.setErrorMsg(errorMsg);
        result.setSentTime(new Date());
        return result;
    }
}
