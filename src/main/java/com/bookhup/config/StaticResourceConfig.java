package com.bookhup.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${upload.posts-dir}")
    private String postsDir;

    @Value("${upload.avatars-dir}")
    private String avatarsDir;

    @Value("${upload.book-covers-dir}")
    private String bookCoversDir;

    @Value("${upload.book-pdf-dir}")
    private String bookPdfDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/posts/**")
                .addResourceLocations("file:" + postsDir)
                .setCachePeriod(3600);

        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:" + avatarsDir)
                .setCachePeriod(3600);

        registry.addResourceHandler("/books/covers/**")
                .addResourceLocations("file:" + bookCoversDir)
                .setCachePeriod(3600);

        registry.addResourceHandler("/books/pdf/**")
                .addResourceLocations("file:" + bookPdfDir)
                .setCachePeriod(3600);
    }
}

