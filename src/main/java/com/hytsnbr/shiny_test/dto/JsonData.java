package com.hytsnbr.shiny_test.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class JsonData {
    
    /** ファイル作成日（エポック日） */
    @JsonProperty("createdAt")
    private final long createdAt = 1;
    
    /** CD情報リスト */
    @JsonProperty("info")
    private List<CDInfo> cdInfoList;
}
