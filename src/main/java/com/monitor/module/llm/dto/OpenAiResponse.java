package com.monitor.module.llm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * OpenAI API响应DTO
 *
 * @author monitor
 */
@Data
public class OpenAiResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应ID
     */
    private String id;

    /**
     * 对象类型
     */
    private String object;

    /**
     * 创建时间
     */
    private Long created;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 选择列表
     */
    private List<Choice> choices;

    /**
     * 使用情况
     */
    private Usage usage;

    /**
     * 选择对象
     */
    @Data
    public static class Choice implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 索引
         */
        private Integer index;

        /**
         * 消息
         */
        private Message message;

        /**
         * 结束原因
         */
        @JsonProperty("finish_reason")
        private String finishReason;
    }

    /**
     * 消息对象
     */
    @Data
    public static class Message implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 角色
         */
        private String role;

        /**
         * 内容
         */
        private String content;
    }

    /**
     * 使用情况
     */
    @Data
    public static class Usage implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 提示token数
         */
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;

        /**
         * 完成token数
         */
        @JsonProperty("completion_tokens")
        private Integer completionTokens;

        /**
         * 总token数
         */
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }
}

