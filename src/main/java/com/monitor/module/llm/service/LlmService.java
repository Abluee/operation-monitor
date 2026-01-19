package com.monitor.module.llm.service;

import com.monitor.module.llm.dto.FieldNameSuggestRequest;
import com.monitor.module.llm.dto.FieldNameSuggestResponse;

/**
 * LLM服务接口
 *
 * @author monitor
 */
public interface LlmService {

    /**
     * 生成字段中文名称建议
     *
     * @param request 请求参数
     * @return 字段名称建议响应
     */
    FieldNameSuggestResponse suggestFieldNames(FieldNameSuggestRequest request);
}

