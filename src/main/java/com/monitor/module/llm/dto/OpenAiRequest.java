package com.monitor.module.llm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * OpenAI API请求DTO
 *
 * @author monitor
 */
@Data
public class OpenAiRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 消息列表
     */
    private List<Message> messages;

    /**
     * 温度参数
     */
    private Double temperature = 0.7;

    /**
     * 最大token数
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    /**
     * 消息对象
     */
    @Data
    public static class Message implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 角色：system, user, assistant
         */
        private String role;

        /**
         * 内容
         */
        private String content;

        public Message() {
        }

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}

