package com.hytsnbr.shiny_test.exception;

import org.apache.commons.lang3.StringUtils;

public class SystemException extends RuntimeException {
    
    private static final String MSG = "システムエラー";
    
    public SystemException() {
        super();
    }
    
    public SystemException(String msg) {
        super(StringUtils.isNotBlank(msg) ? msg : MSG);
    }
    
    public SystemException(Exception exception) {
        super(exception.getMessage());
    }
}
