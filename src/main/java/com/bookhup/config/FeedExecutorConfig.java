package com.bookhup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class FeedExecutorConfig {

    @Bean(name = "logExecutor")
    public Executor taskExecutor() {
        return Executors.newFixedThreadPool(4);
    }
    @Bean(name = "weightExecutor")
    public ThreadPoolTaskExecutor weightExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);          // 4 thread xử lý ML
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(5000);
        executor.setThreadNamePrefix("weight-");
        executor.initialize();
        return executor;
    }
}


