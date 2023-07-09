package com.hytsnbr.shiny_test.exception;

import org.apache.commons.lang3.StringUtils;

public class SystemException extends RuntimeException {
    
    private static final String MSG = "システムエラー";
    
    public SystemException(String msg) {
        this(msg, null);
    }
    
    public SystemException(Exception exception) {
        this(null, exception);
    }
    
    public SystemException(String msg, Exception exception) {
        super(StringUtils.isNotBlank(msg) ? msg : MSG, exception);
    }
}
