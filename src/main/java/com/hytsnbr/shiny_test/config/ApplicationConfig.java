package com.hytsnbr.shiny_test.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "app-config")
public class ApplicationConfig {
    
    /** 対象URL */
    private final String targetUrl;
    
    /** JSONファイル生成先パス */
    private final String jsonPath;
}
