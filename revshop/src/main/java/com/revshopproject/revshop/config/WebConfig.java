package com.revshopproject.revshop.config;

import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Image serving is handled by ImageController
    // which detects content type from file bytes

	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded product images from the local disk folder
        String uploadPath = Paths.get("product-images").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/product-images/**")
                .addResourceLocations(uploadPath + "/");
    }
}
