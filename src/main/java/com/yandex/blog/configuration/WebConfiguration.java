package com.yandex.blog.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Value("${image.upload.dir}")
    private String imagesPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String fullPath = Paths.get(imagesPath).toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/images/**").addResourceLocations(fullPath);
    }
}
