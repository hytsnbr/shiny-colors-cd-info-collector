package com.hytsnbr.shiny_colors.util;

import java.io.IOException;
import java.util.Objects;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hytsnbr.shiny_colors.config.ApplicationConfig;
import com.hytsnbr.shiny_colors.exception.SystemException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JsoupUtilTest {

    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(JsoupUtilTest.class);

    /** jsoupタイムアウト値 */
    private static final int JSOUP_TIMEOUT = 5000;

    /** jsoup接続先URL */
    private static final String JSOUP_TARGET_URL = "https://example.com";

    @Mock private ApplicationConfig mockedAppConfig;

    @Mock private ApplicationConfig.Jsoup mockedJsoupConfig;

    private MockedStatic<Jsoup> mockedJsoup;

    @BeforeAll
    static void setUpAll() {
        logger.info("{} 開始", JsoupUtilTest.class.getSimpleName());
    }

    @AfterEach
    void tearDown() {
        if (Objects.nonNull(mockedJsoup)) {
            mockedJsoup.close();
        }
    }

    @AfterAll
    static void tearDownAll() {
        logger.info("{} 終了", JsoupUtilTest.class.getSimpleName());
    }

    @Test
    @DisplayName("JsoupUtil：正常系 コンストラクタ 呼び出しテスト")
    void コンストラクタ_呼び出しテスト() throws Exception {
        logger.info("JsoupUtil：正常系 コンストラクタ 呼び出しテスト");

        // 実行
        var constructor = JsoupUtil.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // 検証
        assertThatThrownBy(constructor::newInstance)
                .hasRootCauseInstanceOf(IllegalStateException.class)
                .hasRootCauseMessage("ユーティリティクラスはインスタンス化できません");
    }

    @Test
    @DisplayName("connectJsoup：正常系テスト")
    void connectJsoup_正常系テスト() throws Exception {
        logger.info("connectJsoup：正常系テスト");

        // jsoup関連設定をモック化
        mockupJsoupConfig();

        // 実行
        var result = JsoupUtil.connectJsoup(mockedAppConfig, JSOUP_TARGET_URL);

        // 検証
        assertThat(result).isNotNull();
        assertThat(result.baseUri()).isEqualTo(JSOUP_TARGET_URL);
    }

    @Test
    @DisplayName("connectJsoup：異常系 HttpStatusException 発生テスト")
    void connectJsoup_HttpStatusExceptionテスト() throws Exception {
        logger.info("connectJsoup：異常系 HttpStatusException 発生テスト");

        // jsoup関連設定をモック化
        mockupJsoupConfig();

        // jsoup connection をモック化
        var mockedJsoupConnection = mockupJsoupConnection();

        // jsoup側の実行結果をモック化
        when(mockedJsoupConnection.get())
                .thenThrow(new HttpStatusException("404", 404, JSOUP_TARGET_URL));

        // 実行・検証
        assertThatThrownBy(() -> JsoupUtil.connectJsoup(mockedAppConfig, JSOUP_TARGET_URL))
                .isInstanceOf(HttpStatusException.class)
                .hasMessageContaining("404");
    }

    @Test
    @DisplayName("connectJsoup：異常系 SystemException 発生テスト")
    void connectJsoup_SystemExceptionテスト() throws Exception {
        logger.info("connectJsoup：異常系 SystemException 発生テスト");

        // jsoup関連設定をモック化
        mockupJsoupConfig();

        // jsoup connection をモック化
        var mockedJsoupConnection = mockupJsoupConnection();

        // jsoup側の実行結果をモック化
        when(mockedJsoupConnection.get()).thenThrow(new IOException("Connection failed"));

        // 実行・検証
        assertThatThrownBy(() -> JsoupUtil.connectJsoup(mockedAppConfig, "https://example.com"))
                .isInstanceOf(SystemException.class)
                .hasCauseInstanceOf(IOException.class);
    }

    /** jsoup関連設定をモック化する */
    private void mockupJsoupConfig() {
        when(mockedAppConfig.getJsoup()).thenReturn(mockedJsoupConfig);
        when(mockedJsoupConfig.getTimeout()).thenReturn(JSOUP_TIMEOUT);
    }

    /**
     * jsoup connection をモック化する
     *
     * @return {@link Connection}
     */
    private Connection mockupJsoupConnection() {
        var mockedJsoupConnection = mock(Connection.class);
        mockedJsoup = mockStatic(Jsoup.class);
        mockedJsoup.when(() -> Jsoup.connect(JSOUP_TARGET_URL)).thenReturn(mockedJsoupConnection);

        when(mockedJsoupConnection.timeout(JSOUP_TIMEOUT)).thenReturn(mockedJsoupConnection);

        return mockedJsoupConnection;
    }
}
