package com.monitor.module.notify.dto;

import lombok.Data;

import java.util.List;

/**
 * Email notification request
 *
 * @author monitor
 */
@Data
public class EmailRequest {

    /**
     * Sender email address
     */
    private String from;

    /**
     * Recipient email addresses
     */
    private List<String> to;

    /**
     * CC email addresses
     */
    private List<String> cc;

    /**
     * Email subject
     */
    private String subject;

    /**
     * Plain text content
     */
    private String content;

    /**
     * HTML content
     */
    private String htmlContent;

    /**
     * Whether it's an HTML email
     */
    private Boolean isHtml = false;

    /**
     * Attachment file path (optional)
     */
    private String attachmentPath;
}
