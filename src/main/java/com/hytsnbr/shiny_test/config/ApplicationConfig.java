package com.hytsnbr.shiny_test.config;

import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * アプリ設定
 */
@SuppressWarnings("UnusedAssignment")
@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "app-config")
public class ApplicationConfig {
    
    /** 対象URL */
    private final String targetUrl;
    
    /** JSONファイル生成先パス */
    private final String jsonDirPath;
    
    /** jsoup 関連 */
    private final Jsoup jsoup;
    
    /** 処理関連 */
    private final Process process;
    
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
        @NotNull
        private int timeout = 30000;
    }
    
    /**
     * 処理関連
     */
    @Getter
    @AllArgsConstructor
    public static class Process {
        
        /**
         * ショップサイト取得時の再試行回数（回）<br>
         * デフォルト値: 5
         */
        @NotNull
        private int retryLimit = 5;
        
        /**
         * ショップサイト取得時のクールタイム（ミリ秒）<br>
         * デフォルト値: 5000（秒）
         */
        @NotNull
        private int coolTime = 5000;
    }
}
