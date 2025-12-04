package com.bookhup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

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


