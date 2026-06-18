package com.hytsnbr.shiny_colors.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationConfigTest {

    @Test
    @DisplayName("ApplicationConfigの各getterが正しい値を返すこと")
    void testApplicationConfigGetters() {
        // 準備
        var batchJobParamNames =
                new ApplicationConfig.BatchJobParamNames("2025-11-03", "2025-11-03T20:00:00");
        var jsoup = new ApplicationConfig.Jsoup(10000);
        var process = new ApplicationConfig.Process(3, 2000);

        // インスタンス化
        var config =
                new ApplicationConfig(
                        true,
                        batchJobParamNames,
                        "https://example.com",
                        "/tmp/json",
                        jsoup,
                        process);

        // 検証
        assertTrue(config.isForce());
        assertEquals("2025-11-03", config.getBatchJobParamNames().getExecuteDate());
        assertEquals("2025-11-03T20:00:00", config.getBatchJobParamNames().getExecuteDatetime());
        assertEquals("https://example.com", config.getTargetUrl());
        assertEquals("/tmp/json", config.getJsonDirPath());
        assertEquals(10000, config.getJsoup().getTimeout());
        assertEquals(3, config.getProcess().getRetryLimit());
        assertEquals(2000, config.getProcess().getCoolTime());
    }

    @Test
    @DisplayName("デフォルト値を持つ内部クラスの動作確認")
    void testInnerClassesDefaultValues() {
        // 準備
        var jsoup = new ApplicationConfig.Jsoup(30000);
        var process = new ApplicationConfig.Process(5, 5000);

        // 検証
        assertEquals(30000, jsoup.getTimeout());
        assertEquals(5, process.getRetryLimit());
        assertEquals(5000, process.getCoolTime());
    }
}
