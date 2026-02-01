package com.bookhup.config;

import com.bookhup.exception.GlobalExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {
    private static final Logger log = LoggerFactory.getLogger(RestTemplateConfig.class);
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .additionalInterceptors((request, body, execution) -> {
                    log.info("Request URI: {}", request.getURI());
                    log.info("Request Body: {}", new String(body));
                    return execution.execute(request, body);
                })
                .build();
    }

    @Bean
    public RestTemplate hugRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(5))
                .additionalInterceptors((request, body, execution) -> {
                    log.info("Request URI: {}", request.getURI());
                    log.info("Request Body: {}", new String(body));
                    return execution.execute(request, body);
                })
                .build();
    }
}

