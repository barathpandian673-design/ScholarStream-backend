package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Exposes the uploaded-papers directory at /uploads/papers/** so the
 * frontend can preview PDFs directly (e.g. inside an iframe) without
 * going through the authenticated download endpoint.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + uploadDir + "/";
        registry.addResourceHandler("/uploads/papers/**")
                .addResourceLocations(location);
    }
}
