package com.hytsnbr.shiny_colors.util;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.hytsnbr.shiny_colors.config.ApplicationConfig;
import com.hytsnbr.shiny_colors.exception.SystemException;

class JsoupUtilTest {

    private MockedStatic<Jsoup> mockedJsoup;

    @AfterEach
    void tearDown() {
        if (mockedJsoup != null) {
            mockedJsoup.close();
        }
    }

    @Test
    void testConnectJsoup_success() throws Exception {
        // === 準備 ===
        ApplicationConfig mockConfig = mock(ApplicationConfig.class);
        ApplicationConfig.JsoupConfig mockJsoupConfig = mock(ApplicationConfig.JsoupConfig.class);

        when(mockConfig.getJsoup()).thenReturn(mockJsoupConfig);
        when(mockJsoupConfig.getTimeout()).thenReturn(5000);

        // Jsoup.connect のモック
        Connection mockConnection = mock(Connection.class);
        Document mockDocument = new Document("https://example.com");

        when(mockConnection.timeout(anyInt())).thenReturn(mockConnection);
        when(mockConnection.get()).thenReturn(mockDocument);

        mockedJsoup = mockStatic(Jsoup.class);
        mockedJsoup.when(() -> Jsoup.connect("https://example.com"))
                   .thenReturn(mockConnection);

        // === 実行 ===
        Document result = JsoupUtil.connectJsoup(mockConfig, "https://example.com");

        // === 検証 ===
        assertThat(result).isNotNull();
        assertThat(result.baseUri()).isEqualTo("https://example.com");

        verify(mockConnection, times(1)).timeout(5000);
        verify(mockConnection, times(1)).get();
    }

    @Test
    void testConnectJsoup_httpStatusException() throws Exception {
        // === 準備 ===
        ApplicationConfig mockConfig = mock(ApplicationConfig.class);
        ApplicationConfig.JsoupConfig mockJsoupConfig = mock(ApplicationConfig.JsoupConfig.class);

        when(mockConfig.getJsoup()).thenReturn(mockJsoupConfig);
        when(mockJsoupConfig.getTimeout()).thenReturn(5000);

        Connection mockConnection = mock(Connection.class);
        mockedJsoup = mockStatic(Jsoup.class);
        mockedJsoup.when(() -> Jsoup.connect("https://example.com"))
                   .thenReturn(mockConnection);

        when(mockConnection.timeout(anyInt())).thenReturn(mockConnection);
        when(mockConnection.get())
                .thenThrow(new HttpStatusException("404", 404, "https://example.com"));

        // === 実行・検証 ===
        assertThatThrownBy(() -> JsoupUtil.connectJsoup(mockConfig, "https://example.com"))
                .isInstanceOf(HttpStatusException.class)
                .hasMessageContaining("404");
    }

    @Test
    void testConnectJsoup_ioException() throws Exception {
        // === 準備 ===
        ApplicationConfig mockConfig = mock(ApplicationConfig.class);
        ApplicationConfig.JsoupConfig mockJsoupConfig = mock(ApplicationConfig.JsoupConfig.class);

        when(mockConfig.getJsoup()).thenReturn(mockJsoupConfig);
        when(mockJsoupConfig.getTimeout()).thenReturn(5000);

        Connection mockConnection = mock(Connection.class);
        mockedJsoup = mockStatic(Jsoup.class);
        mockedJsoup.when(() -> Jsoup.connect("https://example.com"))
                   .thenReturn(mockConnection);

        when(mockConnection.timeout(anyInt())).thenReturn(mockConnection);
        when(mockConnection.get()).thenThrow(new IOException("connection failed"));

        // === 実行・検証 ===
        assertThatThrownBy(() -> JsoupUtil.connectJsoup(mockConfig, "https://example.com"))
                .isInstanceOf(SystemException.class)
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    void testPrivateConstructor_shouldThrowException() throws Exception {
        // === 検証 ===
        var constructor = JsoupUtil.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertThatThrownBy(constructor::newInstance)
                .hasRootCauseInstanceOf(IllegalStateException.class)
                .hasRootCauseMessage("ユーティリティクラスはインスタンス化できません");
    }
}
