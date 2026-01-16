package com.monitor.module.notify.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monitor.module.notify.dto.DingTalkRequest;
import com.monitor.module.notify.dto.NotifyRequest;
import com.monitor.module.notify.dto.NotifyResult;
import com.monitor.module.notify.entity.BizNotifyRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DingTalk notification channel
 *
 * @author monitor
 */
@Slf4j
@Component
public class DingTalkChannel implements NotifyChannel {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String CHANNEL_NAME = "dingtalk";
    private static final String DINGTALK_API_URL = "https://oapi.dingtalk.com/robot/send";

    public DingTalkChannel(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getChannelName() {
        return CHANNEL_NAME;
    }

    @Override
    public NotifyResult send(NotifyRequest request) {
        log.info("Sending DingTalk notification for task: {}", request.getTaskId());

        try {
            DingTalkRequest dingTalkRequest = buildDingTalkRequest(request);
            String url = DINGTALK_API_URL + "?access_token=" + dingTalkRequest.getAccessToken();

            Map<String, Object> body = buildRequestBody(dingTalkRequest);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getBody() != null && "0".equals(response.getBody().get("errcode").toString())) {
                log.info("DingTalk notification sent successfully for task: {}", request.getTaskId());
                return NotifyResult.success(CHANNEL_NAME, "sent successfully");
            } else {
                String errorMsg = response.getBody() != null ?
                        response.getBody().get("errmsg").toString() : "Unknown error";
                log.error("DingTalk notification failed for task: {}, error: {}",
                        request.getTaskId(), errorMsg);
                return NotifyResult.fail(CHANNEL_NAME, errorMsg);
            }
        } catch (Exception e) {
            log.error("DingTalk notification exception for task: {}", request.getTaskId(), e);
            return NotifyResult.fail(CHANNEL_NAME, e.getMessage());
        }
    }

    @Override
    public boolean isEnabled() {
        // Check if DingTalk is configured (access token is set)
        // This should be configured through NotifyConfig
        return true;
    }

    /**
     * Build DingTalk request from notification request
     */
    private DingTalkRequest buildDingTalkRequest(NotifyRequest request) {
        DingTalkRequest dingTalkRequest = new DingTalkRequest();

        // Build content with task info and violations
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("【监控告警】\n");
        contentBuilder.append("任务: ").append(request.getTaskName()).append("\n");
        contentBuilder.append("时间: ").append(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ).append("\n\n");

        if (request.getThresholdViolations() != null && !request.getThresholdViolations().isEmpty()) {
            contentBuilder.append("阈值违规:\n");
            for (Map<String, Object> violation : request.getThresholdViolations()) {
                contentBuilder.append("- ").append(violation.get("field"))
                        .append(": ").append(violation.get("value"))
                        .append(" (阈值: ").append(violation.get("threshold")).append(")\n");
            }
        }

        if (request.getCompleteReason() != null && !request.getCompleteReason().isEmpty()) {
            contentBuilder.append("\n完成原因: ").append(request.getCompleteReason());
        }

        dingTalkRequest.setContent(contentBuilder.toString());

        // Set default values (should be configured)
        dingTalkRequest.setAccessToken("your-access-token");
        dingTalkRequest.setKeyword("监控告警");
        dingTalkRequest.setIsAtAll(false);

        return dingTalkRequest;
    }

    /**
     * Build DingTalk API request body
     */
    private Map<String, Object> buildRequestBody(DingTalkRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("msgtype", "text");

        Map<String, Object> text = new HashMap<>();
        text.put("content", request.getKeyword() + "\n" + request.getContent());
        body.put("text", text);

        // Handle @mentions
        if (request.getMentionedList() != null && !request.getMentionedList().isEmpty() ||
                request.getMentionedMobileList() != null && !request.getMentionedMobileList().isEmpty()) {
            Map<String, Object> at = new HashMap<>();
            at.put("atUserIds", request.getMentionedList());
            at.put("atMobiles", request.getMentionedMobileList());
            at.put("isAtAll", request.getIsAtAll() != null && request.getIsAtAll());
            body.put("at", at);
        }

        return body;
    }
}
