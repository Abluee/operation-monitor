package com.monitor.module.llm.controller;

import com.monitor.common.result.Result;
import com.monitor.module.llm.dto.FieldNameSuggestRequest;
import com.monitor.module.llm.dto.FieldNameSuggestResponse;
import com.monitor.module.llm.service.LlmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * LLM智能服务控制器
 *
 * @author monitor
 */
@RestController
@RequestMapping("/api/llm")
@Api(tags = "LLM智能服务")
public class LlmController {

    private final LlmService llmService;

    public LlmController(LlmService llmService) {
        this.llmService = llmService;
    }

    /**
     * 生成SQL字段中文名称建议
     *
     * @param request 请求参数
     * @return 字段名称建议
     */
    @PostMapping("/suggest-field-names")
    @ApiOperation("生成SQL字段中文名称建议")
    public Result<FieldNameSuggestResponse> suggestFieldNames(
            @Valid @RequestBody @ApiParam("字段名称建议请求") FieldNameSuggestRequest request) {
        FieldNameSuggestResponse response = llmService.suggestFieldNames(request);
        return Result.success(response);
    }
}

