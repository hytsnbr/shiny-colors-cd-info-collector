package com.hytsnbr.shiny_colors.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/** ディスコグラフィー情報リスト */
@JsonPropertyOrder({
    "createdAt",
    "discographyDataList"
})
public class DiscographyDataListJson extends BaseJsonData {
    
    /** CD情報リスト */
    @JsonProperty("discographyDataList")
    private final List<DiscographyData> discographyDataList;
    
    /** ファクトリーメソッド用コンストラクタ（プライベート） */
    private DiscographyDataListJson(List<DiscographyData> discographyDataList) {
        this.discographyDataList = discographyDataList;
    }
    
    /** ファクトリーメソッド */
    @JsonCreator
    public static DiscographyDataListJson of(
        @JsonProperty("discographyDataList") List<DiscographyData> discographyDataList
    ) {
        return new DiscographyDataListJson(discographyDataList);
    }
    
    public List<DiscographyData> getDiscographyDataList() {
        return this.discographyDataList;
    }
}
