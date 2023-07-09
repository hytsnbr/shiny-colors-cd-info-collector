package com.hytsnbr.shiny_test.dto;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** ショップ情報 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreSite {
    
    /** ショップ名 */
    private String name;
    
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
            return StringUtils.equals(this.name, storeSite.name);
        }
        
        return super.equals(obj);
    }
}