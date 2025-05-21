package com.yandex.blog;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.yandex.blog")
@PropertySource("classpath:application.properties")
public class WebConfiguration implements WebMvcConfigurer {

    @Value("${image.upload.dir}")
    private String imagesPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String fullPath = Paths.get(imagesPath).toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/images/**").addResourceLocations(fullPath);

        registry.addResourceHandler("/js/**").addResourceLocations("/js/");
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
