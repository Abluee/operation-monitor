package com.monitor.module.notify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monitor.module.notify.channel.DingTalkChannel;
import com.monitor.module.notify.channel.EmailChannel;
import com.monitor.module.notify.channel.NotifyChannel;
import com.monitor.module.notify.channel.WebhookChannel;
import com.monitor.module.notify.dto.*;
import com.monitor.module.notify.entity.BizNotifyRecord;
import com.monitor.module.notify.mapper.BizNotifyRecordMapper;
import com.monitor.module.notify.service.NotifyService;
import com.monitor.module.notify.util.ContentBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Notification service implementation
 *
 * @author monitor
 */
@Slf4j
@Service
public class NotifyServiceImpl extends ServiceImpl<BizNotifyRecordMapper, BizNotifyRecord> implements NotifyService {

    private final BizNotifyRecordMapper bizNotifyRecordMapper;
    private final ObjectMapper objectMapper;
    private final ContentBuilder contentBuilder;
    private final Map<String, NotifyChannel> channelMap;

    private static final int MAX_RETRY_COUNT = 3;
    private static final int DEFAULT_PAGE_SIZE = 10;

    public NotifyServiceImpl(
            BizNotifyRecordMapper bizNotifyRecordMapper,
            ObjectMapper objectMapper,
            ContentBuilder contentBuilder,
            List<NotifyChannel> channels) {
        this.bizNotifyRecordMapper = bizNotifyRecordMapper;
        this.objectMapper = objectMapper;
        this.contentBuilder = contentBuilder;

        // Initialize channel map
        this.channelMap = channels.stream()
                .collect(Collectors.toMap(
                        NotifyChannel::getChannelName,
                        channel -> channel,
                        (existing, replacement) -> existing
                ));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<NotifyResult> sendNotify(NotifyRequest request) {
        log.info("Sending notification for task: {}, channels: {}",
                request.getTaskId(), request.getChannel());

        List<NotifyResult> results = new ArrayList<>();

        // Build notification content
        String content = buildNotifyContent(request);

        // Determine channels to send
        List<String> channels = determineChannels(request);

        for (String channelName : channels) {
            NotifyChannel channel = channelMap.get(channelName);
            if (channel == null || !channel.isEnabled()) {
                log.warn("Channel {} is not available or disabled", channelName);
                continue;
            }

            // Create notification record
            BizNotifyRecord record = createNotifyRecord(request, channelName, content);

            try {
                // Build channel-specific request
                NotifyRequest channelRequest = new NotifyRequest();
                BeanUtils.copyProperties(request, channelRequest);
                channelRequest.setChannel(channelName);

                // Send notification
                NotifyResult result = channel.send(channelRequest);

                // Update record status
                updateRecordWithResult(record, result);

                results.add(result);
            } catch (Exception e) {
                log.error("Failed to send notification via channel: {}", channelName, e);
                NotifyResult failResult = NotifyResult.fail(channelName, e.getMessage());
                updateRecordWithResult(record, failResult);
                results.add(failResult);
            }
        }

        log.info("Notification sent for task: {}, results: {}", request.getTaskId(), results.size());
        return results;
    }

    @Override
    public NotifyResult sendDingTalk(DingTalkRequest request) {
        log.info("Sending DingTalk notification");

        try {
            // Build NotifyRequest from DingTalkRequest
            NotifyRequest notifyRequest = new NotifyRequest();
            notifyRequest.setChannel("dingtalk");
            Map<String, Object> dingTalkData = new HashMap<>();
            dingTalkData.put("content", request.getContent());
            notifyRequest.setDataSummary(dingTalkData);

            // Use DingTalk channel
            DingTalkChannel dingTalkChannel = (DingTalkChannel) channelMap.get("dingtalk");
            if (dingTalkChannel != null) {
                return dingTalkChannel.send(notifyRequest);
            }

            return NotifyResult.fail("dingtalk", "Channel not available");
        } catch (Exception e) {
            log.error("Failed to send DingTalk notification", e);
            return NotifyResult.fail("dingtalk", e.getMessage());
        }
    }

    @Override
    public NotifyResult sendEmail(EmailRequest request) {
        log.info("Sending email notification");

        try {
            // Build NotifyRequest from EmailRequest
            NotifyRequest notifyRequest = new NotifyRequest();
            notifyRequest.setChannel("email");
            Map<String, Object> emailData = new HashMap<>();
            emailData.put("subject", request.getSubject());
            emailData.put("content", request.getContent());
            emailData.put("htmlContent", request.getHtmlContent());
            notifyRequest.setDataSummary(emailData);

            // Use Email channel
            EmailChannel emailChannel = (EmailChannel) channelMap.get("email");
            if (emailChannel != null) {
                return emailChannel.send(notifyRequest);
            }

            return NotifyResult.fail("email", "Channel not available");
        } catch (Exception e) {
            log.error("Failed to send email notification", e);
            return NotifyResult.fail("email", e.getMessage());
        }
    }

    @Override
    public NotifyResult sendWebhook(String url, String content) {
        log.info("Sending webhook notification to: {}", url);

        try {
            WebhookChannel webhookChannel = (WebhookChannel) channelMap.get("webhook");
            if (webhookChannel != null) {
                return webhookChannel.sendWebhook(url, content);
            }

            return NotifyResult.fail("webhook", "Channel not available");
        } catch (Exception e) {
            log.error("Failed to send webhook notification", e);
            return NotifyResult.fail("webhook", e.getMessage());
        }
    }

    @Override
    public List<BizNotifyRecord> getNotifyRecords(Long taskId) {
        log.info("Getting notification records for task: {}", taskId);
        return bizNotifyRecordMapper.selectByTaskId(taskId);
    }

    @Override
    public Map<String, Object> getNotifyRecords(Long taskId, Integer pageNum, Integer pageSize) {
        log.info("Getting notification records for task: {}, page: {}, size: {}",
                taskId, pageNum, pageSize);

        Page<BizNotifyRecord> page = new Page<>(pageNum != null ? pageNum : 1,
                pageSize != null ? pageSize : DEFAULT_PAGE_SIZE);

        LambdaQueryWrapper<BizNotifyRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizNotifyRecord::getTaskId, taskId);
        queryWrapper.orderByDesc(BizNotifyRecord::getCreateTime);

        IPage<BizNotifyRecord> pageResult = bizNotifyRecordMapper.selectPage(page, queryWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("pageNum", pageResult.getCurrent());
        result.put("pageSize", pageResult.getSize());
        result.put("list", pageResult.getRecords());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<NotifyResult> retryFailedNotifies(Long taskId) {
        log.info("Retrying failed notifications for task: {}", taskId);

        List<NotifyResult> results = new ArrayList<>();

        // Get failed records for retry
        List<BizNotifyRecord> failedRecords = bizNotifyRecordMapper.selectFailedRecordsForRetry(
                taskId, BizNotifyRecord.Status.FAILED, MAX_RETRY_COUNT);

        if (CollectionUtils.isEmpty(failedRecords)) {
            log.info("No failed records to retry for task: {}", taskId);
            return results;
        }

        for (BizNotifyRecord record : failedRecords) {
            try {
                // Update status to retrying
                record.setStatus(BizNotifyRecord.Status.RETRYING);
                record.setRetryCount(record.getRetryCount() + 1);
                record.setUpdateTime(new Date());
                bizNotifyRecordMapper.updateById(record);

                // Get channel and resend
                NotifyChannel channel = channelMap.get(record.getChannel());
                if (channel == null || !channel.isEnabled()) {
                    log.warn("Channel {} is not available for retry", record.getChannel());
                    updateRecordStatus(record.getId(), BizNotifyRecord.Status.FAILED,
                            "Channel not available");
                    continue;
                }

                // Rebuild request from record
                NotifyRequest request = rebuildRequestFromRecord(record);
                NotifyResult result = channel.send(request);

                // Update record with result
                updateRecordWithResult(record, result);
                results.add(result);

            } catch (Exception e) {
                log.error("Failed to retry notification record: {}", record.getId(), e);
                updateRecordStatus(record.getId(), BizNotifyRecord.Status.FAILED, e.getMessage());
                results.add(NotifyResult.fail(record.getChannel(), e.getMessage()));
            }
        }

        log.info("Retry completed for task: {}, results: {}", taskId, results.size());
        return results;
    }

    @Override
    public List<String> getAvailableChannels() {
        return channelMap.values().stream()
                .filter(NotifyChannel::isEnabled)
                .map(NotifyChannel::getChannelName)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveRecord(BizNotifyRecord record) {
        if (record.getCreateTime() == null) {
            record.setCreateTime(new Date());
        }
        record.setUpdateTime(new Date());
        if (record.getRetryCount() == null) {
            record.setRetryCount(0);
        }

        bizNotifyRecordMapper.insert(record);
        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRecordStatus(Long id, Integer status, String errorMsg) {
        BizNotifyRecord record = new BizNotifyRecord();
        record.setId(id);
        record.setStatus(status);
        record.setErrorMsg(errorMsg);
        record.setUpdateTime(new Date());

        if (status == BizNotifyRecord.Status.SENT) {
            record.setSendTime(new Date());
        }

        bizNotifyRecordMapper.updateById(record);
    }

    /**
     * Build notification content from request
     */
    private String buildNotifyContent(NotifyRequest request) {
        try {
            Map<String, Object> contentMap = new HashMap<>();
            contentMap.put("taskId", request.getTaskId());
            contentMap.put("taskName", request.getTaskName());
            contentMap.put("notifyTime", request.getNotifyTime() != null ?
                    request.getNotifyTime().toString() : new Date().toString());

            if (!CollectionUtils.isEmpty(request.getThresholdViolations())) {
                contentMap.put("thresholdViolations", request.getThresholdViolations());
            }

            if (StringUtils.hasText(request.getCompleteReason())) {
                contentMap.put("completeReason", request.getCompleteReason());
            }

            if (request.getDataSummary() != null && !request.getDataSummary().isEmpty()) {
                contentMap.put("dataSummary", request.getDataSummary());
            }

            return objectMapper.writeValueAsString(contentMap);
        } catch (JsonProcessingException e) {
            log.error("Failed to build notification content", e);
            return "{}";
        }
    }

    /**
     * Determine channels to send notification
     */
    private List<String> determineChannels(NotifyRequest request) {
        if (StringUtils.hasText(request.getChannel())) {
            return Collections.singletonList(request.getChannel());
        }

        // Use configured channels
        return getAvailableChannels();
    }

    /**
     * Create notification record
     */
    private BizNotifyRecord createNotifyRecord(NotifyRequest request, String channelName, String content) {
        BizNotifyRecord record = new BizNotifyRecord();
        record.setTaskId(request.getTaskId());
        record.setTaskName(request.getTaskName());
        record.setChannel(channelName);
        record.setContent(content);
        record.setStatus(BizNotifyRecord.Status.PENDING);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        record.setRetryCount(0);

        // Determine notify type
        if (!CollectionUtils.isEmpty(request.getThresholdViolations())) {
            record.setNotifyType(BizNotifyRecord.NotifyType.THRESHOLD_ALERT);
        } else if (StringUtils.hasText(request.getCompleteReason())) {
            record.setNotifyType(BizNotifyRecord.NotifyType.TASK_COMPLETE);
        } else {
            record.setNotifyType(BizNotifyRecord.NotifyType.CUSTOM);
        }

        bizNotifyRecordMapper.insert(record);
        return record;
    }

    /**
     * Update record with send result
     */
    private void updateRecordWithResult(BizNotifyRecord record, NotifyResult result) {
        if (result.getSuccess()) {
            record.setStatus(BizNotifyRecord.Status.SENT);
            record.setSendTime(new Date());
        } else {
            record.setStatus(BizNotifyRecord.Status.FAILED);
            record.setErrorMsg(result.getErrorMsg());
        }
        record.setUpdateTime(new Date());
        bizNotifyRecordMapper.updateById(record);
    }

    /**
     * Rebuild notification request from record
     */
    private NotifyRequest rebuildRequestFromRecord(BizNotifyRecord record) {
        NotifyRequest request = new NotifyRequest();
        request.setTaskId(record.getTaskId());
        request.setTaskName(record.getTaskName());
        request.setChannel(record.getChannel());
        request.setRecordHistory(false);

        if (StringUtils.hasText(record.getContent())) {
            try {
                Map<String, Object> contentMap = objectMapper.readValue(record.getContent(), Map.class);

                if (contentMap.containsKey("thresholdViolations")) {
                    request.setThresholdViolations((List) contentMap.get("thresholdViolations"));
                }
                if (contentMap.containsKey("completeReason")) {
                    request.setCompleteReason((String) contentMap.get("completeReason"));
                }
                if (contentMap.containsKey("dataSummary")) {
                    request.setDataSummary((Map) contentMap.get("dataSummary"));
                }
            } catch (IOException e) {
                log.error("解析通知记录内容失败，recordId: {}", record.getId(), e);
            }
        }

        return request;
    }
}
