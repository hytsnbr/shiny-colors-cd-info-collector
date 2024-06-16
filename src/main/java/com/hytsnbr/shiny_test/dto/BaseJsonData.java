package com.hytsnbr.shiny_test.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonProperty;

/** JSON関連基底クラス */
@Getter
public abstract class BaseJsonData {
    
    /** ファイル作成日（エポック日） */
    @JsonProperty("createdAt")
    protected final long createdAt = LocalDateTime
        .now()
        .atZone(ZoneId.systemDefault())
        .toEpochSecond();
}
