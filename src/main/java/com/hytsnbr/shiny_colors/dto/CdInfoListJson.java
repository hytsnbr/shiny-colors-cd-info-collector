package com.hytsnbr.shiny_colors.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/** CD情報リスト */
@JsonPropertyOrder({"createdAt", "info"})
public class CdInfoListJson extends BaseJsonData {

    /** CD情報リスト */
    @JsonProperty("info")
    private final List<CdInfo> cdInfoList;

    /** ファクトリーメソッド用コンストラクタ（プライベート） */
    private CdInfoListJson(List<CdInfo> cdInfoList) {
        this.cdInfoList = cdInfoList;
    }

    /** ファクトリーメソッド */
    @JsonCreator
    public static CdInfoListJson of(@JsonProperty("info") List<CdInfo> cdInfoList) {
        return new CdInfoListJson(cdInfoList);
    }

    public List<CdInfo> getCdInfoList() {
        return this.cdInfoList;
    }
}
