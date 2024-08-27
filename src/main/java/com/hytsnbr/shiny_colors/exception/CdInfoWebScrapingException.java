package com.hytsnbr.shiny_colors.exception;

import org.apache.commons.lang3.StringUtils;

/** CD情報取得時Webスクレイピング例外クラス */
public class CdInfoWebScrapingException extends Exception {
    
    private static final String MSG = "スクレイピングエラー";
    
    public CdInfoWebScrapingException(String msg) {
        this(msg, null);
    }
    
    public CdInfoWebScrapingException(Exception exception) {
        this(null, exception);
    }
    
    public CdInfoWebScrapingException(String msg, Exception exception) {
        super(StringUtils.isNotBlank(msg) ? msg : MSG, exception);
    }
}
