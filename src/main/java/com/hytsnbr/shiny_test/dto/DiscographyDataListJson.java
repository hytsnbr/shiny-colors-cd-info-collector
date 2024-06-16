package com.hytsnbr.shiny_test.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/** ディスコグラフィー情報リスト */
@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@JsonPropertyOrder({
    "createdAt",
    "discographyDataList"
})
public class DiscographyDataListJson extends BaseJsonData {
    
    /** CD情報リスト */
    @JsonProperty("discographyDataList")
    private List<DiscographyData> discographyDataList;
}
