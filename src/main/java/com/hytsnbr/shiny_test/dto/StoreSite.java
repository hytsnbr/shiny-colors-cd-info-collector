package com.hytsnbr.shiny_test.dto;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/** ショップ情報 */
@JsonDeserialize(builder = StoreSite.StoreSiteBuilder.class)
@JsonPropertyOrder({
    "name",
    "url",
    "isHiRes"
})
public class StoreSite {
    
    /** ショップ名 */
    private final String name;
    
    /** ショップページURL */
    private final String url;
    
    /** ハイレゾ対応可否 */
    @JsonProperty("isHiRes")
    private final boolean isHiRes;
    
    /** ビルダークラス用コンストラクタ（プライベート） */
    private StoreSite(StoreSiteBuilder builder) {
        this.name = builder.name;
        this.url = builder.url;
        this.isHiRes = builder.isHiRes;
    }
    
    /** ビルダー */
    public static StoreSiteBuilder builder() {
        return new StoreSiteBuilder();
    }
    
    @JsonGetter("isHiRes")
    private boolean isHiRes() {
        return this.isHiRes;
    }
    
    // NOTE: 未定義の場合 SonarLint が警告を出すので対策として定義
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StoreSite storeSite)) {
            return super.equals(obj);
        }
        
        // NOTE: ショップサイトURLはページ読み込みごとに異なるクエリパラメータが付与される事例があったので比較対象にしない
        if (!StringUtils.equals(this.name, storeSite.name)) return false;
        return this.isHiRes == storeSite.isHiRes;
    }
    
    /** ビルダークラス */
    @JsonPOJOBuilder(withPrefix = "")
    public static class StoreSiteBuilder {
        
        private String name;
        
        private String url;
        
        private boolean isHiRes;
        
        /** ショップ名 */
        public StoreSiteBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        /** ショップページURL */
        public StoreSiteBuilder url(String url) {
            this.url = url;
            return this;
        }
        
        /** ハイレゾ対応可否 */
        @JsonProperty("isHiRes")
        public StoreSiteBuilder isHiRes(boolean isHiRes) {
            this.isHiRes = isHiRes;
            return this;
        }
        
        /** ビルド */
        public StoreSite build() {
            return new StoreSite(this);
        }
    }
}