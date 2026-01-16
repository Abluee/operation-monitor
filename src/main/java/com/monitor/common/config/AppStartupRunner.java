package com.monitor.common.config;

import com.monitor.module.task.scheduler.TaskScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 应用启动后初始化
 *
 * @author monitor
 */
@Slf4j
@Component
public class AppStartupRunner implements CommandLineRunner {

    private final TaskScheduler taskScheduler;

    public AppStartupRunner(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void run(String... args) {
        log.info("加载定时任务...");
        taskScheduler.loadTasksFromDatabase();
    }
}
