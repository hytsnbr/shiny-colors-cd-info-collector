package com.hytsnbr.shiny_test.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/** CD情報リスト */
@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@JsonPropertyOrder({
    "createdAt",
    "info"
})
public class CdInfoListJson extends BaseJsonData {
    
    /** CD情報リスト */
    @JsonProperty("info")
    private List<CdInfo> cdInfoList;
}
