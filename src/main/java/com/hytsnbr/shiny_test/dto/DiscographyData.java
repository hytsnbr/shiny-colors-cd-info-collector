package com.hytsnbr.shiny_test.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/** ディスコグラフィー情報 */
@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@JsonPropertyOrder({
    "url",
    "seriesName"
})
public class DiscographyData {
    
    /** CD詳細URL */
    @JsonProperty("url")
    private String url;
    
    /** シリーズ名 */
    @JsonProperty("seriesName")
    private String seriesName;
}
