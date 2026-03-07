package com.hytsnbr.shiny_colors.constant;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StoreTest {

    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(StoreTest.class);

    @BeforeAll
    static void setUpAll() {
        logger.info("{} 開始", JobNameTest.class.getSimpleName());
    }

    @AfterAll
    static void tearDownAll() {
        logger.info("{} 終了", JobNameTest.class.getSimpleName());
    }

    @Test
    @DisplayName("getByHtmlName：正常系テスト")
    void getByHtmlName_正常系テスト() {
        final var targetHtmlName = "Yodobashi";
        final var excepted = Store.YODOBASHI;
        final var actual = Store.getByHtmlName(targetHtmlName);

        assertEquals(excepted, actual);
    }

    @Test
    @DisplayName("getByHtmlName：異常系テスト")
    void getByHtmlName_異常系テスト() {
        final var targetHtmlName = "TEST";

        assertThrows(IllegalArgumentException.class, () -> Store.getByHtmlName(targetHtmlName));
    }

    @Test
    @DisplayName("getName：取得テスト")
    void getName_取得テスト() {
        final var excepted = "アソビストア";
        final var actual = Store.ASOBI_STORE.getName();

        assertEquals(excepted, actual);
    }

    @Test
    @DisplayName("getIncludeQueryParams：取得テスト")
    void getIncludeQueryParams_取得テスト() {
        final var excepted = List.of();
        final var actual = Store.ASOBI_STORE.getIncludeQueryParams();

        assertEquals(excepted, actual);
    }
}
