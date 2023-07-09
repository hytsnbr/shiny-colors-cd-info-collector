package com.hytsnbr.shiny_test.dto;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

/** CD情報 */
@Getter
@Setter
@NoArgsConstructor
public class CDInfo {
    
    /** CDタイトル */
    private String title;
    
    /** 品番 */
    private String recordNumber;
    
    /** リリース日 */
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    
    /** CDジャケット画像URL */
    private String jacketUrl;
    
    /** 限定販売か */
    private boolean limited;
    
    /** シリーズ名 */
    private String series;
    
    /** アーティスト名 */
    private String artist;
    
    /** ダウンロード・ストリーミングサイトリスト */
    private List<StoreSite> downloadSiteList = Collections.emptyList();
    
    /** CD販売サイトリスト */
    private List<StoreSite> purchaseSiteList = Collections.emptyList();
    
    // NOTE: 未定義の場合 SonarLint が警告を出すので対策として定義
    @SuppressWarnings("EmptyMethod")
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CDInfo cdInfo)) {
            return super.equals(obj);
        }
        
        if (!StringUtils.equals(this.title, cdInfo.title)) return false;
        if (!StringUtils.equals(this.recordNumber, cdInfo.recordNumber)) return false;
        if (!Objects.equals(this.releaseDate, cdInfo.releaseDate)) return false;
        if (!StringUtils.equals(this.jacketUrl, cdInfo.jacketUrl)) return false;
        if (this.limited != cdInfo.limited) return false;
        if (!StringUtils.equals(this.series, cdInfo.series)) return false;
        if (!StringUtils.equals(this.artist, cdInfo.artist)) return false;
        if (this.downloadSiteList.size() != cdInfo.downloadSiteList.size()) return false;
        for (var storeSite : cdInfo.downloadSiteList) {
            if (!this.downloadSiteList.contains(storeSite)) {
                return false;
            }
        }
        if (this.purchaseSiteList.size() != cdInfo.purchaseSiteList.size()) return false;
        for (var storeSite : cdInfo.purchaseSiteList) {
            if (!this.purchaseSiteList.contains(storeSite)) {
                return false;
            }
        }
        
        return true;
    }
}