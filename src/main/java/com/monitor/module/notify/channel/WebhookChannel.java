package com.monitor.module.notify.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monitor.module.notify.dto.NotifyRequest;
import com.monitor.module.notify.dto.NotifyResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Webhook notification channel
 *
 * @author monitor
 */
@Slf4j
@Component
public class WebhookChannel implements NotifyChannel {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String CHANNEL_NAME = "webhook";

    public WebhookChannel(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getChannelName() {
        return CHANNEL_NAME;
    }

    @Override
    public NotifyResult send(NotifyRequest request) {
        log.info("Sending webhook notification for task: {}", request.getTaskId());

        try {
            String url = "https://webhook.example.com/notify"; // Should be configured
            String content = buildWebhookContent(request);

            return sendWebhook(url, content);

        } catch (Exception e) {
            log.error("Webhook notification exception for task: {}", request.getTaskId(), e);
            return NotifyResult.fail(CHANNEL_NAME, e.getMessage());
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Send webhook notification
     *
     * @param url     Webhook URL
     * @param content Request content
     * @return Send result
     */
    public NotifyResult sendWebhook(String url, String content) {
        log.info("Sending webhook to: {}", url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(content, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Webhook notification sent successfully");
                return NotifyResult.success(CHANNEL_NAME, "sent successfully");
            } else {
                String errorMsg = response.getBody() != null ?
                        response.getBody().toString() : "HTTP " + response.getStatusCode().value();
                log.error("Webhook notification failed, error: {}", errorMsg);
                return NotifyResult.fail(CHANNEL_NAME, errorMsg);
            }
        } catch (Exception e) {
            log.error("Webhook notification exception", e);
            return NotifyResult.fail(CHANNEL_NAME, e.getMessage());
        }
    }

    /**
     * Build webhook content
     */
    private String buildWebhookContent(NotifyRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("event", "monitor_alert");
        body.put("taskId", request.getTaskId());
        body.put("taskName", request.getTaskName());
        body.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        body.put("notifyTime", request.getNotifyTime() != null ?
                request.getNotifyTime().toInstant().toString() : LocalDateTime.now().toString());

        if (request.getThresholdViolations() != null && !request.getThresholdViolations().isEmpty()) {
            body.put("thresholdViolations", request.getThresholdViolations());
        }

        if (request.getCompleteReason() != null && !request.getCompleteReason().isEmpty()) {
            body.put("completeReason", request.getCompleteReason());
        }

        if (request.getDataSummary() != null && !request.getDataSummary().isEmpty()) {
            body.put("dataSummary", request.getDataSummary());
        }

        try {
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            log.error("Failed to serialize webhook content", e);
            return "{}";
        }
    }
}
