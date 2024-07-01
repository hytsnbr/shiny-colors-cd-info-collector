package com.hytsnbr.shiny_test.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonProperty;

/** JSON関連基底クラス */
public abstract class BaseJsonData {
    
    /** ファイル作成日（エポック日） */
    @JsonProperty("createdAt")
    protected final long createdAt = LocalDateTime
        .now()
        .atZone(ZoneId.systemDefault())
        .toEpochSecond();
    
    public long getCreatedAt() {
        return this.createdAt;
    }
}
