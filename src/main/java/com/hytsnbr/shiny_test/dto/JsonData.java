package com.hytsnbr.shiny_test.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@JsonPropertyOrder({
    "createdAt",
    "info"
})
public class JsonData {
    
    /** ファイル作成日（エポック日） */
    @JsonProperty("createdAt")
    private final long createdAt = LocalDate.now().toEpochDay();
    
    /** CD情報リスト */
    @JsonProperty("info")
    private List<CDInfo> cdInfoList;
}
