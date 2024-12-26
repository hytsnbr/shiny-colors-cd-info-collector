package com.hytsnbr.shiny_colors.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/** ディスコグラフィー情報 */
@JsonPropertyOrder({"url", "seriesName"})
public class DiscographyData {

    /** CD詳細URL */
    @JsonProperty("url")
    private final String url;

    /** シリーズ名 */
    @JsonProperty("seriesName")
    private final String seriesName;

    /** ファクトリーメソッド用コンストラクタ（プライベート） */
    private DiscographyData(String url, String seriesName) {
        this.url = url;
        this.seriesName = seriesName;
    }

    /** ファクトリーメソッド */
    @JsonCreator
    public static DiscographyData of(
            @JsonProperty("url") String url, @JsonProperty("seriesName") String seriesName) {
        return new DiscographyData(url, seriesName);
    }

    public String getUrl() {
        return this.url;
    }

    public String getSeriesName() {
        return this.seriesName;
    }
}
