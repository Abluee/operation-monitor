package com.monitor.module.notify.dto;

import lombok.Data;

import java.util.List;

/**
 * Notification configuration DTO
 *
 * @author monitor
 */
@Data
public class NotifyConfigDTO {

    /**
     * Enabled notification channels
     */
    private List<String> channels;

    /**
     * Notification users (for DingTalk)
     */
    private List<String> notifyUsers;

    /**
     * Mentioned users (for DingTalk @mentions)
     */
    private List<String> mentionUsers;

    /**
     * Notification interval in minutes (to avoid duplicate notifications)
     */
    private Integer interval;

    /**
     * DingTalk configuration
     */
    private DingTalkConfig dingTalk;

    /**
     * Email configuration
     */
    private EmailConfig email;

    /**
     * Webhook configuration
     */
    private WebhookConfig webhook;

    @Data
    public static class DingTalkConfig {
        private String accessToken;
        private String keyword;
        private Boolean enabled = true;
    }

    @Data
    public static class EmailConfig {
        private String host;
        private Integer port;
        private String username;
        private String password;
        private String from;
        private Boolean enabled = true;
    }

    @Data
    public static class WebhookConfig {
        private String url;
        private String method = "POST";
        private String contentType = "application/json";
        private Boolean enabled = true;
    }
}
