package com.hytsnbr.shiny_test.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * アプリ設定
 */
@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "app-config")
public class ApplicationConfig {
    
    /** 対象URL */
    private final String targetUrl;
    
    /** JSONファイル生成先パス */
    private final String jsonPath;
    
    /** jsoup 関連 */
    private final Jsoup jsoup;
    
    /**
     * jsoup 関連
     */
    @Getter
    @AllArgsConstructor
    public static class Jsoup {
        
        /**
         * タイムアウト秒数（ミリ秒)<br>
         * デフォルト値: 30000（30秒）
         */
        private Integer timeout = 30000;
    }
}
