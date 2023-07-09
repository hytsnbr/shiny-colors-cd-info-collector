package com.hytsnbr.shiny_test.dto;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.hytsnbr.shiny_test.constant.Store;

/** ショップ情報 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreSite {
    
    private Store store;
    
    /** ショップ名 */
    @JsonGetter("name")
    public String getStoreName() {
        Objects.requireNonNull(this.store);
        
        return this.store.getStoreName();
    }
    
    /** ショップページURL */
    private String url;
    
    // NOTE: 未定義の場合 SonarLint が警告を出すので対策として定義
    @SuppressWarnings("EmptyMethod")
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StoreSite storeSite) {
            // NOTE: ショップサイトURLはページ読み込みごとに異なるクエリパラメータが付与される事例があったので比較対象にしない
            return this.store == storeSite.store;
        }
        
        return super.equals(obj);
    }
}