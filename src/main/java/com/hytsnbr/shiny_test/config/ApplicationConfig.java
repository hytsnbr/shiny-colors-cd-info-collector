package com.hytsnbr.shiny_test.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app-config")
public class ApplicationConfig {
    
    /** 対象URL */
    private String targetUrl;
    
    /** JSONファイル生成先パス */
    private String jsonPath;
}
