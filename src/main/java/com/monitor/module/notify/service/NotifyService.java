package com.monitor.module.notify.service;

import com.monitor.module.notify.dto.*;
import com.monitor.module.notify.entity.BizNotifyRecord;

import java.util.List;
import java.util.Map;

/**
 * Notification service interface
 *
 * @author monitor
 */
public interface NotifyService {

    /**
     * Send notification
     *
     * @param request Notification request
     * @return List of send results
     */
    List<NotifyResult> sendNotify(NotifyRequest request);

    /**
     * Send DingTalk notification
     *
     * @param request DingTalk request
     * @return Send result
     */
    NotifyResult sendDingTalk(DingTalkRequest request);

    /**
     * Send email notification
     *
     * @param request Email request
     * @return Send result
     */
    NotifyResult sendEmail(EmailRequest request);

    /**
     * Send webhook notification
     *
     * @param url     Webhook URL
     * @param content Request content
     * @return Send result
     */
    NotifyResult sendWebhook(String url, String content);

    /**
     * Get notification records by task ID
     *
     * @param taskId Task ID
     * @return List of notification records
     */
    List<BizNotifyRecord> getNotifyRecords(Long taskId);

    /**
     * Get notification records by task ID with pagination
     *
     * @param taskId Task ID
     * @param pageNum Page number
     * @param pageSize Page size
     * @return Page of notification records
     */
    Map<String, Object> getNotifyRecords(Long taskId, Integer pageNum, Integer pageSize);

    /**
     * Retry failed notifications for a task
     *
     * @param taskId Task ID
     * @return Retry results
     */
    List<NotifyResult> retryFailedNotifies(Long taskId);

    /**
     * Get available notification channels
     *
     * @return List of channel names
     */
    List<String> getAvailableChannels();

    /**
     * Save notification record
     *
     * @param record Notification record
     * @return Record ID
     */
    Long saveRecord(BizNotifyRecord record);

    /**
     * Update notification record status
     *
     * @param id      Record ID
     * @param status  New status
     * @param errorMsg Error message (if failed)
     */
    void updateRecordStatus(Long id, Integer status, String errorMsg);
}
