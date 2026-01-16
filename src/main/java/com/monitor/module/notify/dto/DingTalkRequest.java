package com.monitor.module.notify.dto;

import lombok.Data;

import java.util.List;

/**
 * DingTalk notification request
 *
 * @author monitor
 */
@Data
public class DingTalkRequest {

    /**
     * Access token for DingTalk robot
     */
    private String accessToken;

    /**
     * Keyword for verification
     */
    private String keyword;

    /**
     * Message content
     */
    private String content;

    /**
     * Mentioned user IDs list
     */
    private List<String> mentionedList;

    /**
     * Mentioned mobile numbers list
     */
    private List<String> mentionedMobileList;

    /**
     * Whether to use at all
     */
    private Boolean isAtAll = false;

    /**
     * Message type (text, markdown, etc.)
     */
    private String msgType = "text";
}
