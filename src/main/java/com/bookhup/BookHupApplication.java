package com.bookhup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BookHupApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookHupApplication.class, args);
    }

}
