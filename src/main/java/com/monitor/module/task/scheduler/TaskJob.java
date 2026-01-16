package com.monitor.module.task.scheduler;

import com.monitor.module.task.mapper.BizTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务执行Job
 * Quartz Job实现类
 *
 * @author monitor
 */
@Slf4j
@Component
public class TaskJob implements Job {

    @Autowired
    private BizTaskMapper bizTaskMapper;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long taskId = context.getJobDetail().getJobDataMap().getLong("taskId");
        String taskCode = context.getJobDetail().getJobDataMap().getString("taskCode");

        log.info("开始执行任务，taskId: {}, taskCode: {}", taskId, taskCode);

        try {
            // 1. 获取任务配置
            com.monitor.module.task.entity.BizTask task = bizTaskMapper.selectById(taskId);
            if (task == null) {
                log.warn("任务不存在，taskId: {}", taskId);
                return;
            }

            // 2. 执行任务逻辑
            executeTask(task);

            // 3. 更新任务状态
            updateTaskStatus(taskId, true);

            log.info("任务执行完成，taskId: {}, taskCode: {}", taskId, taskCode);
        } catch (Exception e) {
            log.error("任务执行失败，taskId: {}, taskCode: {}", taskId, taskCode, e);
            // 更新任务状态为失败
            updateTaskStatus(taskId, false);
            throw new JobExecutionException(e);
        }
    }

    /**
     * 执行任务
     *
     * @param task 任务
     */
    private void executeTask(com.monitor.module.task.entity.BizTask task) {
        // TODO: 实现任务执行逻辑
        // 1. 执行SQL查询
        // 2. 应用阈值规则
        // 3. 应用完成规则
        // 4. 记录执行结果

        log.info("执行任务逻辑，taskId: {}", task.getId());
    }

    /**
     * 更新任务状态
     *
     * @param taskId    任务ID
     * @param success   是否成功
     */
    private void updateTaskStatus(Long taskId, boolean success) {
        BizTaskMapper mapper = this.bizTaskMapper;

        com.monitor.module.task.entity.BizTask task = new com.monitor.module.task.entity.BizTask();
        task.setId(taskId);
        task.setLastExecTime(new Date());
        task.setUpdateTime(new Date());
        mapper.updateById(task);

        log.info("更新任务执行时间，taskId: {}, success: {}", taskId, success);
    }
}
