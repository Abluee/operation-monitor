package com.monitor.module.execute.controller;

import com.monitor.common.result.Result;
import com.monitor.common.utils.DateUtils;
import com.monitor.module.execute.dto.*;
import com.monitor.module.execute.service.ExecuteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 执行控制器
 *
 * @author monitor
 */
@Slf4j
@RestController
@RequestMapping("/api/execute")
@Api(tags = "任务执行管理")
@Validated
public class ExecuteController {

    private final ExecuteService executeService;

    public ExecuteController(ExecuteService executeService) {
        this.executeService = executeService;
    }

    /**
     * 验证任务（请求体包含taskId）
     *
     * @param request 验证请求
     * @return 验证结果
     */
    @PostMapping("/verify")
    @ApiOperation("验证任务")
    public Result<VerifyResult> verify(@RequestBody @ApiParam("验证请求") VerifyRequest request) {
        log.info("验证任务请求，taskId: {}", request.getTaskId());
        VerifyResult result = executeService.verify(request.getTaskId());
        return Result.success(result);
    }

    /**
     * 验证任务（路径参数包含taskId）
     *
     * @param taskId 任务ID
     * @return 验证结果
     */
    @PostMapping("/verify/{taskId}")
    @ApiOperation("验证任务（路径参数）")
    public Result<VerifyResult> verifyByPath(@PathVariable @ApiParam("任务ID") Long taskId) {
        log.info("验证任务请求，taskId: {}", taskId);
        VerifyResult result = executeService.verify(taskId);
        return Result.success(result);
    }

    /**
     * 获取执行结果
     *
     * @param taskId 任务ID
     * @return 执行结果列表
     */
    @GetMapping("/result/{taskId}")
    @ApiOperation("获取执行结果")
    public Result<List<Map<String, Object>>> getExecutionResult(@PathVariable @ApiParam("任务ID") Long taskId) {
        log.info("获取执行结果请求，taskId: {}", taskId);
        List<Map<String, Object>> result = executeService.getExecutionResult(taskId);
        return Result.success(result);
    }

    /**
     * 获取最近一次执行结果
     *
     * @param taskId 任务ID
     * @return 最近一次执行结果
     */
    @GetMapping("/latest/{taskId}")
    @ApiOperation("获取最近一次执行结果")
    public Result<Map<String, Object>> getLatestExecution(@PathVariable @ApiParam("任务ID") Long taskId) {
        log.info("获取最近一次执行结果请求，taskId: {}", taskId);
        Map<String, Object> result = executeService.getLatestExecution(taskId);
        return Result.success(result);
    }

    /**
     * 获取执行日志
     *
     * @param taskId 任务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param execStatus 执行状态
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 执行日志列表
     */
    @GetMapping("/log/{taskId}")
    @ApiOperation("获取执行日志")
    public Result<Map<String, Object>> getExecutionLogs(
            @PathVariable @ApiParam("任务ID") Long taskId,
            @RequestParam(required = false) @ApiParam("开始时间") String startTime,
            @RequestParam(required = false) @ApiParam("结束时间") String endTime,
            @RequestParam(required = false) @ApiParam("执行状态") Integer execStatus,
            @RequestParam(required = false, defaultValue = "1") @ApiParam("页码") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") @ApiParam("每页大小") Integer pageSize) {
        log.info("获取执行日志请求，taskId: {}, startTime: {}, endTime: {}", taskId, startTime, endTime);

        ExecutionLogDTO dto = new ExecutionLogDTO();
        dto.setTaskId(taskId);
        if (startTime != null && !startTime.isEmpty()) {
            dto.setStartTime(DateUtils.parseUtilDate(startTime, DateUtils.DEFAULT_PATTERN));
        }
        if (endTime != null && !endTime.isEmpty()) {
            dto.setEndTime(DateUtils.parseUtilDate(endTime, DateUtils.DEFAULT_PATTERN));
        }
        dto.setExecStatus(execStatus);
        dto.setPageNum(pageNum);
        dto.setPageSize(pageSize);

        Map<String, Object> result = executeService.getExecutionLogs(dto);
        return Result.success(result);
    }

    /**
     * 执行任务
     *
     * @param request 执行请求
     * @return 执行结果
     */
    @PostMapping
    @ApiOperation("执行任务")
    public Result<ExecuteResult> execute(@RequestBody @ApiParam("执行请求") ExecuteRequest request) {
        log.info("执行任务请求，taskId: {}, timeRange: {}", request.getTaskId(), request.getTimeRange());
        ExecuteResult result = executeService.execute(request);
        return Result.success(result);
    }
}
