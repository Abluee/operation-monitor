package com.monitor.module.type.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 验证配置DTO
 *
 * @author monitor
 */
@Data
public class VerifyConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否启用验证
     */
    private Boolean enabled;

    /**
     * 验证类型：http, sql, script
     */
    private String verifyType;

    /**
     * 验证端点（HTTP验证时使用）
     */
    private String endpoint;

    /**
     * 请求方法：GET, POST, PUT, DELETE
     */
    private String method;

    /**
     * 超时时间（毫秒）
     */
    private Integer timeout;

    /**
     * 重试次数
     */
    private Integer retryTimes;

    /**
     * 请求头配置
     */
    private Map<String, String> headers;

    /**
     * 请求体配置
     */
    private Object requestBody;

    /**
     * 响应映射配置
     */
    private Map<String, Object> responseMapping;

    /**
     * 成功判断条件
     */
    private String successCondition;
}
