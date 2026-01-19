package com.monitor.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * LLM配置类
 *
 * @author monitor
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "monitor.llm")
public class LlmConfig {

    /**
     * API基础URL
     */
    private String baseUrl = "https://api.xiaomimimo.com";

    /**
     * API密钥
     */
    private String apiKey;

    /**
     * 模型名称
     */
    private String model = "mimo-v2-flash";

    /**
     * 请求超时时间（毫秒）
     */
    private Integer timeout = 60000;

    /**
     * 最大token数
     */
    private Integer maxTokens = 2000;
}

