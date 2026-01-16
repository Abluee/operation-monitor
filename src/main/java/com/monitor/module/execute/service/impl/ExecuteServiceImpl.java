package com.monitor.module.execute.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.monitor.common.enums.ExecStatusEnum;
import com.monitor.common.enums.TriggerTypeEnum;
import com.monitor.common.exception.BizException;
import com.monitor.common.utils.JsonUtils;
import com.monitor.module.execute.dto.*;
import com.monitor.module.execute.entity.BizExecResult;
import com.monitor.module.execute.entity.BizTaskLog;
import com.monitor.module.execute.mapper.BizExecResultMapper;
import com.monitor.module.execute.mapper.BizTaskLogMapper;
import com.monitor.module.execute.service.ExecuteService;
import com.monitor.module.execute.util.CompleteChecker;
import com.monitor.module.execute.util.SqlBuilder;
import com.monitor.module.execute.util.ThresholdChecker;
import com.monitor.module.type.entity.BizType;
import com.monitor.module.type.mapper.BizTypeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * 执行服务实现
 *
 * @author monitor
 */
@Slf4j
@Service
public class ExecuteServiceImpl extends ServiceImpl<BizTaskLogMapper, BizTaskLog> implements ExecuteService {

    @Autowired
    private BizExecResultMapper bizExecResultMapper;

    @Autowired
    private BizTypeMapper bizTypeMapper;

    @Autowired
    private DataSource dataSource;

    private static final int MAX_ROWS = 10000;

    @Override
    public VerifyResult verify(Long taskId) {
        log.info("开始验证任务，taskId: {}", taskId);

        VerifyResult result = new VerifyResult();
        try {
            // 获取任务配置
            Map<String, Object> taskConfig = getTaskConfig(taskId);
            BizType type = getBizType((Long) taskConfig.get("typeId"));

            // 构建并执行SQL
            String sql = buildExecuteSql(type.getSqlContent(), null, null);
            List<Map<String, Object>> dataList = executeQuery(sql);

            // 处理结果
            if (CollectionUtils.isEmpty(dataList)) {
                result.setValue(0);
                result.setDetail(Collections.emptyList());
                result.setIsCompleted(false);
                result.setCompletedReason("无数据");
                return result;
            }

            // 设置详细数据（限制数量）
            List<Object> details = new ArrayList<>();
            int limit = Math.min(dataList.size(), 100);
            for (int i = 0; i < limit; i++) {
                details.add(dataList.get(i));
            }
            result.setDetail(details);

            // 计算汇总值
            Object sumValue = calculateSummary(dataList);
            result.setValue(sumValue);

            // 检查阈值
            List<ThresholdResult> thresholdResults = checkThresholds(dataList, type.getVerifyConfig());
            result.setThresholdResults(thresholdResults);

            // 检查完成条件
            CompleteChecker.CompleteCheckResult completeResult = checkCompletion(dataList, type.getFormula());
            result.setIsCompleted(completeResult.isCompleted());
            result.setCompletedReason(completeResult.getReason());

            log.info("任务验证完成，taskId: {}, 数据条数: {}", taskId, dataList.size());
            return result;

        } catch (BizException e) {
            log.error("验证任务失败，taskId: {}", taskId, e);
            throw e;
        } catch (Exception e) {
            log.error("验证任务异常，taskId: {}", taskId, e);
            throw new BizException("验证任务失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExecuteResult execute(ExecuteRequest request) {
        log.info("开始执行任务，taskId: {}, timeRange: {}", request.getTaskId(), request.getTimeRange());

        ExecuteResult result = new ExecuteResult();
        long startTime = System.currentTimeMillis();
        Date execTime = new Date();

        try {
            // 获取任务配置
            Map<String, Object> taskConfig = getTaskConfig(request.getTaskId());
            BizType type = getBizType((Long) taskConfig.get("typeId"));

            // 构建SQL
            String sql = buildExecuteSql(type.getSqlContent(), request.getTimeRange(), null);

            // 执行SQL
            List<Map<String, Object>> dataList = executeQuery(sql);

            // 计算执行耗时
            long duration = System.currentTimeMillis() - startTime;

            // 处理结果
            Integer dataCount = dataList.size();
            result.setTaskId(request.getTaskId());
            result.setExecTime(execTime);
            result.setDurationMs(duration);
            result.setDataCount(dataCount);

            // 检查阈值
            List<ThresholdResult> thresholdResults = checkThresholds(dataList, type.getVerifyConfig());
            int violationCount = (int) thresholdResults.stream().filter(ThresholdResult::getTriggered).count();
            result.setThresholdViolations(violationCount);

            // 检查完成条件
            CompleteChecker.CompleteCheckResult completeResult = checkCompletion(dataList, type.getFormula());
            result.setIsCompleted(completeResult.isCompleted());
            result.setCompletedReason(completeResult.getReason());

            // 保存执行日志
            BizTaskLog taskLog = saveExecutionLog(request.getTaskId(), type.getId(), execTime, ExecStatusEnum.SUCCESS.getCode(),
                    TriggerTypeEnum.MANUAL.getCode(), sql, dataList, null, duration);

            // 保存执行结果
            saveExecutionResult(request.getTaskId(), type.getId(), execTime, dataList, thresholdResults, completeResult);

            result.setSuccess(true);
            result.setMessage("执行成功");

            log.info("任务执行完成，taskId: {}, 数据条数: {}, 耗时: {}ms", request.getTaskId(), dataCount, duration);
            return result;

        } catch (BizException e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("执行任务业务异常，taskId: {}", request.getTaskId(), e);

            result.setTaskId(request.getTaskId());
            result.setExecTime(execTime);
            result.setSuccess(false);
            result.setDurationMs(duration);
            result.setMessage("执行失败: " + e.getMessage());

            // 保存失败日志
            saveExecutionLog(request.getTaskId(), null, execTime, ExecStatusEnum.FAIL.getCode(),
                    TriggerTypeEnum.MANUAL.getCode(), null, null, e.getMessage(), duration);

            throw e;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("执行任务异常，taskId: {}", request.getTaskId(), e);

            result.setTaskId(request.getTaskId());
            result.setExecTime(execTime);
            result.setSuccess(false);
            result.setDurationMs(duration);
            result.setMessage("执行异常: " + e.getMessage());
            throw new BizException("执行任务失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getExecutionLogs(ExecutionLogDTO dto) {
        log.info("查询执行日志，taskId: {}, startTime: {}, endTime: {}", dto.getTaskId(), dto.getStartTime(), dto.getEndTime());

        LambdaQueryWrapper<BizTaskLog> queryWrapper = new LambdaQueryWrapper<>();

        if (dto.getTaskId() != null) {
            queryWrapper.eq(BizTaskLog::getTaskId, dto.getTaskId());
        }
        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            queryWrapper.ge(BizTaskLog::getExecTime, dto.getStartTime());
            queryWrapper.le(BizTaskLog::getExecTime, dto.getEndTime());
        }
        if (dto.getExecStatus() != null) {
            queryWrapper.eq(BizTaskLog::getExecStatus, dto.getExecStatus());
        }

        queryWrapper.orderByDesc(BizTaskLog::getExecTime);

        int pageNum = dto.getPageNum() != null ? dto.getPageNum() : 1;
        int pageSize = dto.getPageSize() != null ? dto.getPageSize() : 10;

        Page<BizTaskLog> page = new Page<>(pageNum, pageSize);
        IPage<BizTaskLog> pageResult = this.baseMapper.selectPage(page, queryWrapper);

        // 转换为VO列表
        List<ExecutionLogVO> voList = new ArrayList<>();
        for (BizTaskLog log : pageResult.getRecords()) {
            ExecutionLogVO vo = new ExecutionLogVO();
            BeanUtils.copyProperties(log, vo);
            voList.add(vo);
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", pageResult.getTotal());
        resultMap.put("pageNum", pageResult.getCurrent());
        resultMap.put("pageSize", pageResult.getSize());
        resultMap.put("list", voList);

        return resultMap;
    }

    @Override
    public List<Map<String, Object>> getExecutionResult(Long taskId) {
        log.info("获取执行结果，taskId: {}", taskId);

        List<BizExecResult> results = bizExecResultMapper.selectByTaskId(taskId);
        if (CollectionUtils.isEmpty(results)) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (BizExecResult result : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", result.getId());
            map.put("taskId", result.getTaskId());
            map.put("typeId", result.getTypeId());
            map.put("execTime", result.getExecTime());
            map.put("dataCount", result.getDataCount());
            map.put("thresholdResult", result.getThresholdResult());
            map.put("isCompleted", result.getIsCompleted());
            map.put("completeReason", result.getCompleteReason());
            map.put("createTime", result.getCreateTime());

            // 解析metricData
            if (StringUtils.hasText(result.getMetricData())) {
                map.put("metricData", JsonUtils.parseToMap(result.getMetricData()));
            }

            resultList.add(map);
        }

        return resultList;
    }

    @Override
    public Map<String, Object> getLatestExecution(Long taskId) {
        log.info("获取最近一次执行结果，taskId: {}", taskId);

        BizExecResult result = bizExecResultMapper.selectLatestByTaskId(taskId);
        if (result == null) {
            return null;
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("id", result.getId());
        resultMap.put("taskId", result.getTaskId());
        resultMap.put("typeId", result.getTypeId());
        resultMap.put("execTime", result.getExecTime());
        resultMap.put("dataCount", result.getDataCount());
        resultMap.put("thresholdResult", result.getThresholdResult());
        resultMap.put("isCompleted", result.getIsCompleted());
        resultMap.put("completeReason", result.getCompleteReason());
        resultMap.put("createTime", result.getCreateTime());

        // 解析metricData
        if (StringUtils.hasText(result.getMetricData())) {
            resultMap.put("metricData", JsonUtils.parseToMap(result.getMetricData()));
        }

        return resultMap;
    }

    // ==================== 私有方法 ====================

    /**
     * 获取任务配置（模拟）
     */
    private Map<String, Object> getTaskConfig(Long taskId) {
        Map<String, Object> config = new HashMap<>();
        config.put("id", taskId);
        config.put("taskName", "任务" + taskId);
        config.put("typeId", 1L);
        return config;
    }

    /**
     * 获取业务类型
     */
    private BizType getBizType(Long typeId) {
        if (typeId == null) {
            return null;
        }
        BizType type = bizTypeMapper.selectById(typeId);
        if (type == null) {
            throw new BizException("类型配置不存在，typeId: " + typeId);
        }
        return type;
    }

    /**
     * 构建执行SQL
     */
    private String buildExecuteSql(String typeSql, String timeRange, String whereClause) {
        // 验证SQL安全性
        if (!SqlBuilder.validateSqlSecurity(typeSql)) {
            throw new BizException("SQL存在安全风险");
        }

        // 替换时间占位符
        String sql = SqlBuilder.replaceTimePlaceholder(typeSql, timeRange);

        // 追加WHERE条件
        if (StringUtils.hasText(whereClause)) {
            sql = SqlBuilder.appendWhereClause(sql, whereClause);
        }

        return sql;
    }

    /**
     * 执行查询SQL
     */
    private List<Map<String, Object>> executeQuery(String sql) {
        if (!StringUtils.hasText(sql)) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            int rowCount = 0;
            while (rs.next() && rowCount < MAX_ROWS) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
                rowCount++;
            }

        } catch (SQLException e) {
            log.error("执行SQL查询失败，SQL: {}", sql, e);
            throw new BizException("SQL执行失败: " + e.getMessage());
        }

        return results;
    }

    /**
     * 计算汇总值
     */
    private Object calculateSummary(List<Map<String, Object>> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return 0;
        }

        Map<String, Object> firstRow = dataList.get(0);
        if (firstRow.isEmpty()) {
            return 0;
        }

        return firstRow.values().iterator().next();
    }

    /**
     * 检查阈值
     */
    private List<ThresholdResult> checkThresholds(List<Map<String, Object>> dataList, String verifyConfigStr) {
        if (CollectionUtils.isEmpty(dataList) || !StringUtils.hasText(verifyConfigStr)) {
            return Collections.emptyList();
        }

        Map<String, Object> verifyConfig = JsonUtils.parseToMap(verifyConfigStr);
        if (verifyConfig == null || !verifyConfig.containsKey("thresholds")) {
            return Collections.emptyList();
        }

        Object thresholdsObj = verifyConfig.get("thresholds");
        if (!(thresholdsObj instanceof List)) {
            return Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> thresholds = (List<Map<String, Object>>) thresholdsObj;
        return ThresholdChecker.check(dataList, thresholds);
    }

    /**
     * 检查完成条件
     */
    private CompleteChecker.CompleteCheckResult checkCompletion(List<Map<String, Object>> dataList, String formula) {
        CompleteChecker.CompleteCheckResult result = new CompleteChecker.CompleteCheckResult();

        if (!StringUtils.hasText(formula)) {
            // 没有完成条件，默认检查是否有数据
            result.setCompleted(!CollectionUtils.isEmpty(dataList));
            result.setReason(CollectionUtils.isEmpty(dataList) ? "无数据" : "数据已就绪");
            result.setDataCount(dataList.size());
            return result;
        }

        try {
            Map<String, Object> rules = JsonUtils.parseToMap(formula);
            return CompleteChecker.check(dataList, rules);
        } catch (Exception e) {
            log.warn("解析完成条件失败，formula: {}", formula, e);
            result.setCompleted(true);
            result.setReason("完成条件解析失败，默认完成");
            return result;
        }
    }

    /**
     * 保存执行日志
     */
    private BizTaskLog saveExecutionLog(Long taskId, Long typeId, Date execTime, Integer execStatus,
                                        Integer triggerType, String execSql, List<Map<String, Object>> dataList,
                                        String errorMsg, long durationMs) {
        BizTaskLog taskLog = new BizTaskLog();
        taskLog.setTaskId(taskId);
        taskLog.setTypeId(typeId);
        taskLog.setExecTime(execTime);
        taskLog.setExecStatus(execStatus);
        taskLog.setTriggerType(triggerType);
        taskLog.setExecSql(execSql);
        taskLog.setErrorMsg(errorMsg);
        taskLog.setDurationMs((int) durationMs);

        if (dataList != null && !dataList.isEmpty()) {
            int limit = Math.min(dataList.size(), 100);
            List<Map<String, Object>> subList = dataList.subList(0, limit);
            taskLog.setExecResult(JsonUtils.toJSONString(subList));
        }

        taskLog.setCreateTime(new Date());
        this.baseMapper.insert(taskLog);

        return taskLog;
    }

    /**
     * 保存执行结果
     */
    private void saveExecutionResult(Long taskId, Long typeId, Date execTime, List<Map<String, Object>> dataList,
                                     List<ThresholdResult> thresholdResults, CompleteChecker.CompleteCheckResult completeResult) {
        BizExecResult execResult = new BizExecResult();
        execResult.setTaskId(taskId);
        execResult.setTypeId(typeId);
        execResult.setExecTime(execTime);
        execResult.setDataCount(dataList != null ? dataList.size() : 0);

        if (dataList != null && !dataList.isEmpty()) {
            execResult.setMetricData(JsonUtils.toJSONString(dataList.get(0)));
        }

        if (thresholdResults != null && !thresholdResults.isEmpty()) {
            execResult.setThresholdResult(JsonUtils.toJSONString(thresholdResults));
        }

        if (completeResult != null) {
            execResult.setIsCompleted(completeResult.isCompleted() ? 1 : 0);
            execResult.setCompleteReason(completeResult.getReason());
        }

        execResult.setCreateTime(new Date());
        bizExecResultMapper.insert(execResult);
    }
}
