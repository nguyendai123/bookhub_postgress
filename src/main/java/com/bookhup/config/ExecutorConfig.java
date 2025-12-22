package com.bookhup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAsync
public class ExecutorConfig {

    // Logging
    @Bean(name = "logExecutor")
    public ExecutorService logExecutor() {
        return new ThreadPoolExecutor(
                2,                      // Core threads
                10,                     // Max threads
                60L, TimeUnit.SECONDS,  // Idle thread timeout
                new LinkedBlockingQueue<>(500),       // Queue size
                new ThreadPoolExecutor.DiscardPolicy() // ❗ Không block API nếu full
        );
    }

    // Feed weight
    @Bean("weightExecutor")
    public ThreadPoolTaskExecutor weightExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("weight-worker-");
        executor.setWaitForTasksToCompleteOnShutdown(true); // ❗ chờ tasks còn lại
        executor.setAwaitTerminationSeconds(10);          // max chờ 10s
        executor.initialize();
        return executor;
    }

    @Bean("notificationExecutor")
    public ThreadPoolTaskExecutor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("notification-worker-");
        executor.setWaitForTasksToCompleteOnShutdown(true); // ❗ chờ tasks còn lại
        executor.setAwaitTerminationSeconds(10);          // max chờ 10s
        executor.initialize();
        return executor;
    }

    @Bean
    public ThreadPoolTaskExecutor fanoutExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);          // ít thread
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(200);       // BẮT BUỘC giới hạn
        executor.setThreadNamePrefix("fanout-");
        executor.initialize();
        return executor;
    }

}


