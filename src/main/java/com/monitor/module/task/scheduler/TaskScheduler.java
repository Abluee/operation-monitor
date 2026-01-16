package com.monitor.module.task.scheduler;

import com.monitor.common.enums.TaskStatusEnum;
import com.monitor.module.task.entity.BizTask;
import com.monitor.module.task.mapper.BizTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 任务调度器
 * 使用Quartz进行任务调度
 *
 * @author monitor
 */
@Slf4j
@Component
public class TaskScheduler {

    private final BizTaskMapper bizTaskMapper;
    private final Scheduler scheduler;

    @Autowired
    public TaskScheduler(BizTaskMapper bizTaskMapper, Scheduler scheduler) {
        this.bizTaskMapper = bizTaskMapper;
        this.scheduler = scheduler;
    }

    /**
     * 调度任务执行
     */
    public void scheduleTask(BizTask task, String cronExpression) {
        try {
            JobKey jobKey = new JobKey("task_" + task.getId(), "taskGroup");
            if (scheduler.checkExists(jobKey)) {
                updateJobCron(task, cronExpression);
                log.info("更新任务调度成功，taskId: {}", task.getId());
            } else {
                createJob(task, cronExpression);
                log.info("创建任务调度成功，taskId: {}", task.getId());
            }
        } catch (SchedulerException e) {
            log.error("调度任务失败，taskId: {}", task.getId(), e);
            throw new RuntimeException("调度任务失败", e);
        }
    }

    private void createJob(BizTask task, String cronExpression) throws SchedulerException {
        JobDetail jobDetail = org.quartz.JobBuilder.newJob(TaskJob.class)
                .withIdentity("task_" + task.getId(), "taskGroup")
                .usingJobData("taskId", task.getId())
                .build();

        org.quartz.Trigger trigger = org.quartz.TriggerBuilder.newTrigger()
                .withIdentity("taskTrigger_" + task.getId(), "taskTriggerGroup")
                .withSchedule(org.quartz.CronScheduleBuilder.cronSchedule(cronExpression))
                .startNow()
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    private void updateJobCron(BizTask task, String cronExpression) throws SchedulerException {
        org.quartz.TriggerKey triggerKey = new org.quartz.TriggerKey("taskTrigger_" + task.getId(), "taskTriggerGroup");
        org.quartz.Trigger trigger = scheduler.getTrigger(triggerKey);
        if (trigger != null) {
            org.quartz.Trigger newTrigger = org.quartz.TriggerBuilder.newTrigger()
                    .withIdentity("taskTrigger_" + task.getId(), "taskTriggerGroup")
                    .withSchedule(org.quartz.CronScheduleBuilder.cronSchedule(cronExpression))
                    .startNow()
                    .build();
            scheduler.rescheduleJob(triggerKey, newTrigger);
        }
    }

    public void pauseTask(Long taskId) {
        try {
            JobKey jobKey = new JobKey("task_" + taskId, "taskGroup");
            if (scheduler.checkExists(jobKey)) {
                scheduler.pauseJob(jobKey);
                log.info("暂停任务成功，taskId: {}", taskId);
            }
        } catch (SchedulerException e) {
            log.error("暂停任务失败", e);
            throw new RuntimeException("暂停任务失败", e);
        }
    }

    public void resumeTask(Long taskId) {
        try {
            JobKey jobKey = new JobKey("task_" + taskId, "taskGroup");
            if (scheduler.checkExists(jobKey)) {
                scheduler.resumeJob(jobKey);
                log.info("恢复任务成功，taskId: {}", taskId);
            }
        } catch (SchedulerException e) {
            log.error("恢复任务失败", e);
            throw new RuntimeException("恢复任务失败", e);
        }
    }

    public void deleteTask(Long taskId) {
        try {
            JobKey jobKey = new JobKey("task_" + taskId, "taskGroup");
            org.quartz.TriggerKey triggerKey = new org.quartz.TriggerKey("taskTrigger_" + taskId, "taskTriggerGroup");

            if (scheduler.checkExists(triggerKey)) {
                scheduler.unscheduleJob(triggerKey);
            }
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
            log.info("删除任务调度成功，taskId: {}", taskId);
        } catch (SchedulerException e) {
            log.error("删除任务调度失败", e);
            throw new RuntimeException("删除任务调度失败", e);
        }
    }

    public void triggerTask(Long taskId) {
        try {
            JobKey jobKey = new JobKey("task_" + taskId, "taskGroup");
            if (scheduler.checkExists(jobKey)) {
                scheduler.triggerJob(jobKey);
                log.info("立即执行任务成功，taskId: {}", taskId);
            }
        } catch (SchedulerException e) {
            log.error("立即执行任务失败", e);
            throw new RuntimeException("立即执行任务失败", e);
        }
    }

    public void loadTasksFromDatabase() {
        try {
            List<BizTask> tasks = bizTaskMapper.selectTasksToExecute();
            for (BizTask task : tasks) {
                if (task.getStatus().equals(TaskStatusEnum.PENDING_ALLOCATION.getCode())) {
                    String cronExpression = generateCronExpression(task);
                    scheduleTask(task, cronExpression);
                }
            }
            log.info("从数据库加载任务完成，共加载 {} 个任务", tasks.size());
        } catch (Exception e) {
            log.error("从数据库加载任务失败", e);
        }
    }

    private String generateCronExpression(BizTask task) {
        return "0 0/5 * * * ?";
    }

    public void updateNextExecTime(Long taskId) {
        try {
            org.quartz.TriggerKey triggerKey = new org.quartz.TriggerKey("taskTrigger_" + taskId, "taskTriggerGroup");
            org.quartz.Trigger trigger = scheduler.getTrigger(triggerKey);
            if (trigger != null) {
                Date nextFireTime = trigger.getNextFireTime();
                if (nextFireTime != null) {
                    BizTask task = new BizTask();
                    task.setId(taskId);
                    task.setNextExecTime(nextFireTime);
                    task.setUpdateTime(new Date());
                    bizTaskMapper.updateById(task);
                }
            }
        } catch (SchedulerException e) {
            log.error("更新任务下次执行时间失败", e);
        }
    }
}
