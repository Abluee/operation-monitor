package com.monitor.module.llm.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monitor.common.config.LlmConfig;
import com.monitor.common.exception.BizException;
import com.monitor.module.llm.dto.*;
import com.monitor.module.llm.service.LlmService;
import com.monitor.module.sqlparse.dto.SqlFieldDTO;
import com.monitor.module.sqlparse.service.SqlParseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LLM服务实现类
 *
 * @author monitor
 */
@Slf4j
@Service
public class LlmServiceImpl implements LlmService {

    private final LlmConfig llmConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final SqlParseService sqlParseService;

    public LlmServiceImpl(LlmConfig llmConfig, RestTemplate restTemplate, 
                          ObjectMapper objectMapper, SqlParseService sqlParseService) {
        this.llmConfig = llmConfig;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.sqlParseService = sqlParseService;
    }

    @Override
    public FieldNameSuggestResponse suggestFieldNames(FieldNameSuggestRequest request) {
        // 使用SQL解析服务获取字段信息
        List<SqlFieldDTO> sqlFields = sqlParseService.queryFieldsFromDb(request.getSql());
        if (sqlFields.isEmpty()) {
            throw new BizException("SQL解析失败，未能获取到字段信息");
        }
        log.info("从SQL解析获取到 {} 个字段: {}", sqlFields.size(), 
                sqlFields.stream().map(SqlFieldDTO::getName).collect(Collectors.joining(", ")));

        // 构建提示词
        String prompt = buildPrompt(request, sqlFields);
        log.debug("LLM prompt: {}", prompt);

        // 调用LLM API
        String llmResponse = callLlmApi(prompt);
        log.debug("LLM response: {}", llmResponse);

        // 解析响应
        return parseResponse(llmResponse, sqlFields);
    }

    /**
     * 构建提示词
     */
    private String buildPrompt(FieldNameSuggestRequest request, List<SqlFieldDTO> sqlFields) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("你是一个数据分析专家，请根据业务场景为SQL查询结果字段生成简洁的中文列名。\n\n");
        
        sb.append("【业务背景】\n");
        sb.append("督办类型：").append(request.getTypeName()).append("\n");
        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            sb.append("督办描述：").append(request.getDescription()).append("\n");
        }
        sb.append("\n");
        
        sb.append("【SQL语句】\n");
        sb.append(request.getSql()).append("\n\n");
        
        sb.append("【字段列表】\n");
        for (int i = 0; i < sqlFields.size(); i++) {
            SqlFieldDTO field = sqlFields.get(i);
            sb.append(i + 1).append(". ").append(field.getName())
              .append(" (类型: ").append(field.getDataType()).append(")\n");
        }
        
        sb.append("\n【任务】\n");
        sb.append("请为上述每个字段生成一个简短的中文名称（2-6个字），用于数据报表展示。\n\n");
        
        sb.append("【输出格式】\n");
        sb.append("请严格按以下JSON数组格式输出，不要添加其他内容：\n");
        sb.append("[{\"name\":\"英文字段名\",\"chineseName\":\"中文名\"}]\n\n");
        
        sb.append("示例输出：\n");
        sb.append("[{\"name\":\"api\",\"chineseName\":\"接口路径\"},{\"name\":\"total\",\"chineseName\":\"总数量\"}]\n");
        
        return sb.toString();
    }

    /**
     * 调用LLM API
     */
    private String callLlmApi(String prompt) {
        String url = llmConfig.getBaseUrl() + "/v1/chat/completions";
        
        // 构建请求体
        OpenAiRequest openAiRequest = new OpenAiRequest();
        openAiRequest.setModel(llmConfig.getModel());
        openAiRequest.setMaxTokens(llmConfig.getMaxTokens());
        openAiRequest.setTemperature(0.3); // 使用较低的温度以获得更稳定的输出
        
        List<OpenAiRequest.Message> messages = new ArrayList<>();
        messages.add(new OpenAiRequest.Message("system", "你是一个专业的数据库字段命名助手，擅长根据业务场景为SQL字段生成合适的中文名称。"));
        messages.add(new OpenAiRequest.Message("user", prompt));
        openAiRequest.setMessages(messages);
        
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + llmConfig.getApiKey());
        
        HttpEntity<OpenAiRequest> entity = new HttpEntity<>(openAiRequest, headers);
        
        try {
            log.info("Calling LLM API: {}", url);
            ResponseEntity<OpenAiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    OpenAiResponse.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                OpenAiResponse openAiResponse = response.getBody();
                if (openAiResponse.getChoices() != null && !openAiResponse.getChoices().isEmpty()) {
                    return openAiResponse.getChoices().get(0).getMessage().getContent();
                }
            }
            
            throw new BizException("LLM API返回结果为空");
        } catch (Exception e) {
            log.error("调用LLM API失败", e);
            throw new BizException("调用LLM API失败: " + e.getMessage());
        }
    }

    /**
     * 解析LLM响应
     */
    private FieldNameSuggestResponse parseResponse(String llmResponse, List<SqlFieldDTO> sqlFields) {
        FieldNameSuggestResponse response = new FieldNameSuggestResponse();
        
        try {
            // 提取JSON部分
            String jsonStr = extractJson(llmResponse);
            log.debug("提取的JSON: {}", jsonStr);
            
            // 解析JSON数组
            List<LlmFieldSuggestion> llmSuggestions = objectMapper.readValue(
                    jsonStr,
                    new TypeReference<List<LlmFieldSuggestion>>() {}
            );
            
            // 转换为响应格式，并补充数据类型
            List<FieldNameSuggestResponse.FieldSuggestion> suggestions = llmSuggestions.stream()
                    .map(s -> {
                        FieldNameSuggestResponse.FieldSuggestion suggestion = new FieldNameSuggestResponse.FieldSuggestion();
                        suggestion.setName(s.getName());
                        suggestion.setChineseName(s.getChineseName());
                        suggestion.setReason(s.getReason());
                        
                        // 查找对应字段的数据类型
                        sqlFields.stream()
                                .filter(f -> f.getName().equals(s.getName()))
                                .findFirst()
                                .ifPresent(f -> suggestion.setDataType(f.getDataType()));
                        
                        return suggestion;
                    })
                    .collect(Collectors.toList());
            
            response.setSuggestions(suggestions);
            
        } catch (Exception e) {
            log.error("解析LLM响应失败: {}", llmResponse, e);
            // 返回默认响应
            response.setSuggestions(createDefaultSuggestions(sqlFields));
        }
        
        return response;
    }

    /**
     * 从LLM响应中提取JSON部分
     */
    private String extractJson(String response) {
        // 尝试提取代码块中的JSON
        if (response.contains("```json")) {
            int start = response.indexOf("```json") + 7;
            int end = response.indexOf("```", start);
            if (end > start) {
                return response.substring(start, end).trim();
            }
        }
        
        // 尝试提取普通代码块
        if (response.contains("```")) {
            int start = response.indexOf("```") + 3;
            // 跳过可能的语言标识
            int lineEnd = response.indexOf("\n", start);
            if (lineEnd > start) {
                start = lineEnd + 1;
            }
            int end = response.indexOf("```", start);
            if (end > start) {
                return response.substring(start, end).trim();
            }
        }
        
        // 尝试直接查找JSON数组
        int start = response.indexOf("[");
        int end = response.lastIndexOf("]");
        if (start >= 0 && end > start) {
            return response.substring(start, end + 1);
        }
        
        return response.trim();
    }

    /**
     * 创建默认的字段建议（当LLM解析失败时使用）
     */
    private List<FieldNameSuggestResponse.FieldSuggestion> createDefaultSuggestions(List<SqlFieldDTO> sqlFields) {
        return sqlFields.stream()
                .map(f -> {
                    FieldNameSuggestResponse.FieldSuggestion suggestion = new FieldNameSuggestResponse.FieldSuggestion();
                    suggestion.setName(f.getName());
                    suggestion.setChineseName(f.getName()); // 使用英文名作为默认
                    suggestion.setDataType(f.getDataType());
                    suggestion.setReason("LLM解析失败，使用默认值");
                    return suggestion;
                })
                .collect(Collectors.toList());
    }

    /**
     * LLM返回的字段建议内部类
     */
    @lombok.Data
    private static class LlmFieldSuggestion {
        private String name;
        private String chineseName;
        private String reason;
    }
}

