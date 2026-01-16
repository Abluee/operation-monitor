package com.monitor.common.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 解析数据源配置
 */
@Configuration
public class ParseDataSourceConfig {

    @Bean(name = "parseDataSource")
    @ConfigurationProperties(prefix = "spring.parse-datasource")
    public DataSource parseDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        return dataSource;
    }
}
