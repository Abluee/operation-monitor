package com.monitor.module.execute.service;

import com.monitor.module.execute.dto.*;

import java.util.List;
import java.util.Map;

/**
 * 执行服务接口
 *
 * @author monitor
 */
public interface ExecuteService {

    /**
     * 验证任务
     * 执行任务并返回验证结果（不保存日志）
     *
     * @param taskId 任务ID
     * @return 验证结果
     */
    VerifyResult verify(Long taskId);

    /**
     * 执行任务
     * 执行任务并保存执行日志和结果
     *
     * @param request 执行请求
     * @return 执行结果
     */
    ExecuteResult execute(ExecuteRequest request);

    /**
     * 获取执行日志列表
     *
     * @param dto 查询条件
     * @return 执行日志列表（分页）
     */
    Map<String, Object> getExecutionLogs(ExecutionLogDTO dto);

    /**
     * 获取执行结果
     *
     * @param taskId 任务ID
     * @return 执行结果列表
     */
    List<Map<String, Object>> getExecutionResult(Long taskId);

    /**
     * 获取最近一次执行结果
     *
     * @param taskId 任务ID
     * @return 最近一次执行结果
     */
    Map<String, Object> getLatestExecution(Long taskId);
}
