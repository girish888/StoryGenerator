package com.example.storyapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Use allowedOriginPatterns for flexible wildcard matching (host+port).
                // In production replace "*" with the exact origin: "https://your-frontend.example"
                .allowedOriginPatterns("*")
                // If you truly need cookies/auth, set this to true and replace "*" above with exact origins.
                .allowCredentials(false)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Disposition") // any headers you want exposed
                .maxAge(3600); // seconds to cache preflight
    }
}
