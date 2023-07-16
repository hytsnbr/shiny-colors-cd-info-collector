package com.hytsnbr.shiny_test.dto;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

/** ショップ情報 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreSite {
    
    /** ショップ名 */
    private String name;
    
    /** ショップページURL */
    private String url;
    
    /** ハイレゾ対応可否 */
    @JsonProperty("isHiRes")
    private boolean isHiRes;
    
    @JsonGetter("url")
    public String getUrl() {
        // NOTE: クエリパラメータを削除
        return UriComponentsBuilder.fromUriString(this.url)
                                   .replaceQueryParams(new LinkedMultiValueMap<>())
                                   .toUriString();
    }
    
    @JsonGetter("isHiRes")
    private boolean isHiRes() {
        return this.isHiRes;
    }
    
    // NOTE: 未定義の場合 SonarLint が警告を出すので対策として定義
    @SuppressWarnings("EmptyMethod")
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
        return this.isHiRes != storeSite.isHiRes;
    }
}