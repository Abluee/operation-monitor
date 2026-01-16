package com.monitor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 任务派发数据督办系统 - 启动类
 *
 * @author monitor
 */
@SpringBootApplication
@MapperScan("com.monitor.module.*.mapper")
@EnableScheduling
public class MonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class, args);
        System.out.println("============================================");
        System.out.println("  任务派发数据督办系统启动成功！");
        System.out.println("  API文档: http://localhost:8080/doc.html");
        System.out.println("============================================");
    }

}
