package com.hytsnbr.shiny_colors.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JsonFileNameTest {

    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(JsonFileNameTest.class);

    @Test
    @DisplayName("JsonFileName：正常系 コンストラクタ 呼び出しテスト")
    void コンストラクタ_呼び出しテスト() throws Exception {
        logger.info("JsonFileName：正常系 コンストラクタ 呼び出しテスト");

        var constructor = JsonFileName.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance)
                .hasRootCauseInstanceOf(IllegalStateException.class)
                .hasRootCauseMessage("このクラスはインスタンス化できません");
    }
}
