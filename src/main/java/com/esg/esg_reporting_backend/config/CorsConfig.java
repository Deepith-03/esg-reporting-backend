package com.esg.esg_reporting_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins:}")
    private String customAllowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        Set<String> allowedOrigins = new LinkedHashSet<>();
        // Default allowed explicit origins
        allowedOrigins.add("http://localhost:5173");
        allowedOrigins.add("http://localhost:3000");
        allowedOrigins.add("http://127.0.0.1:5173");
        allowedOrigins.add("https://esg-reporting-frontend-u2je1k3a4-future-transformation.vercel.app");

        // Parse custom configured origins from environment / application.properties
        if (StringUtils.hasText(customAllowedOrigins)) {
            Arrays.stream(customAllowedOrigins.split(","))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .forEach(allowedOrigins::add);
        }

        configuration.setAllowedOrigins(new ArrayList<>(allowedOrigins));

        // Pattern matching for Vercel preview environments and localhost dynamic ports
        configuration.setAllowedOriginPatterns(List.of(
            "https://*.vercel.app",
            "http://localhost:[*]",
            "http://127.0.0.1:[*]"
        ));

        configuration.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));

        configuration.setExposedHeaders(List.of(
            "Authorization",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));

        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

