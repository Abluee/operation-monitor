package com.monitor.module.task.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Job工厂
 * 用于创建Quartz Job实例
 *
 * @author monitor
 */
@Slf4j
@Component
public class MonitorJobFactory implements JobFactory, ApplicationContextAware {

    private final AutowireCapableBeanFactory beanFactory;
    private ApplicationContext applicationContext;

    public MonitorJobFactory(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public org.quartz.Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        // 获取Job类
        Class<? extends org.quartz.Job> jobClass = bundle.getJobDetail().getJobClass();

        try {
            // 获取Spring容器中的Bean
            String beanName = jobClass.getSimpleName();
            if (applicationContext.containsBean(beanName)) {
                return applicationContext.getBean(beanName, jobClass);
            }

            // 如果容器中没有，则创建新实例并自动注入
            org.quartz.Job job = jobClass.newInstance();
            beanFactory.autowireBean(job);
            return job;
        } catch (Exception e) {
            log.error("创建Job实例失败，jobClass: {}", jobClass.getName(), e);
            throw new SchedulerException("创建Job实例失败", e);
        }
    }
}
