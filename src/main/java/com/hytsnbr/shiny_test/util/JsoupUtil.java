package com.hytsnbr.shiny_test.util;

import java.io.IOException;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hytsnbr.shiny_test.config.ApplicationConfig;
import com.hytsnbr.shiny_test.exception.SystemException;

/** Jsoup関連ユーティリティクラス */
public final class JsoupUtil {
    
    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(JsoupUtil.class);
    
    private JsoupUtil() {
        throw new IllegalStateException("ユーティリティクラスはインスタンス化できません");
    }
    
    /**
     * ページ取得
     *
     * @param url ページURL
     *
     * @throws HttpStatusException ページ情報の取得に失敗した場合
     */
    public static Document connectJsoup(ApplicationConfig appConfig, String url) throws HttpStatusException {
        final var timeout = appConfig.getJsoup().getTimeout();
        logger.debug("接続先: {} / タイムアウト設定: {}", url, timeout);
        
        try {
            var connection = Jsoup.connect(url);
            connection.timeout(timeout);
            
            return connection.get();
        } catch (HttpStatusException e) {
            throw e;
        } catch (IOException e) {
            throw new SystemException(e);
        }
    }
}
