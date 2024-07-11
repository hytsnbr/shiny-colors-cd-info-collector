package com.hytsnbr.shiny_test.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

/** CD情報 */
@JsonDeserialize(builder = CdInfo.CdInfoBuilder.class)
@JsonPropertyOrder({
    "title",
    "recordNumbers",
    "releaseDate",
    "cdInfoPageUrl",
    "jacketUrl",
    "limited",
    "series",
    "artist",
    "downloadSiteList",
    "purchaseSiteList"
})
public class CdInfo {
    
    /** CDタイトル */
    private final String title;
    
    /** 品番 */
    private final List<String> recordNumbers;
    
    /** リリース日 */
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate releaseDate;
    
    /** CD情報ページURL */
    private final String cdInfoPageUrl;
    
    /** CDジャケット画像URL */
    private final String jacketUrl;
    
    /** 限定販売か */
    private final boolean limited;
    
    /** シリーズ名 */
    private final String series;
    
    /** アーティスト名 */
    private final String artist;
    
    /** ダウンロード・ストリーミングサイトリスト */
    private final List<StoreSite> downloadSiteList;
    
    /** CD販売サイトリスト */
    private final List<StoreSite> purchaseSiteList;
    
    /** ビルダークラス用コンストラクタ（プライベート） */
    private CdInfo(CdInfoBuilder builder) {
        title = builder.title;
        recordNumbers = builder.recordNumbers;
        releaseDate = builder.releaseDate;
        cdInfoPageUrl = builder.cdInfoPageUrl;
        jacketUrl = builder.jacketUrl;
        limited = builder.limited;
        series = builder.series;
        artist = builder.artist;
        downloadSiteList = builder.downloadSiteList;
        purchaseSiteList = builder.purchaseSiteList;
    }
    
    /** ビルダー取得 */
    public static CdInfoBuilder builder() {
        return new CdInfoBuilder();
    }
    
    // NOTE: 未定義の場合 SonarLint が警告を出すので対策として定義
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CdInfo cdInfo)) {
            return super.equals(obj);
        }
        
        if (!StringUtils.equals(this.title, cdInfo.title)) return false;
        if (this.recordNumbers.size() != cdInfo.recordNumbers.size()) return false;
        for (var recordNumber : cdInfo.recordNumbers) {
            if (!this.recordNumbers.contains(recordNumber)) {
                return false;
            }
        }
        if (!Objects.equals(this.releaseDate, cdInfo.releaseDate)) return false;
        if (!StringUtils.equals(this.cdInfoPageUrl, cdInfo.cdInfoPageUrl)) return false;
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
    
    @SuppressWarnings("unused")
    public String getTitle() {
        return this.title;
    }
    
    @SuppressWarnings("unused")
    public List<String> getRecordNumbers() {
        return this.recordNumbers;
    }
    
    @SuppressWarnings("unused")
    public LocalDate getReleaseDate() {
        return this.releaseDate;
    }
    
    @SuppressWarnings("unused")
    public String getCdInfoPageUrl() {
        return this.cdInfoPageUrl;
    }
    
    @SuppressWarnings("unused")
    public String getJacketUrl() {
        return this.jacketUrl;
    }
    
    @SuppressWarnings("unused")
    public boolean isLimited() {
        return this.limited;
    }
    
    @SuppressWarnings("unused")
    public String getSeries() {
        return this.series;
    }
    
    @SuppressWarnings("unused")
    public String getArtist() {
        return this.artist;
    }
    
    @SuppressWarnings("unused")
    public List<StoreSite> getDownloadSiteList() {
        return Objects.nonNull(this.downloadSiteList) ? this.downloadSiteList : List.of();
    }
    
    @SuppressWarnings("unused")
    public List<StoreSite> getPurchaseSiteList() {
        return Objects.nonNull(this.purchaseSiteList) ? this.purchaseSiteList : List.of();
    }
    
    /** ビルダークラス */
    @JsonPOJOBuilder(withPrefix = "")
    public static class CdInfoBuilder {
        
        private String title;
        
        private List<String> recordNumbers;
        
        private LocalDate releaseDate;
        
        private String cdInfoPageUrl;
        
        private String jacketUrl;
        
        private boolean limited;
        
        private String series;
        
        private String artist;
        
        private List<StoreSite> downloadSiteList;
        
        private List<StoreSite> purchaseSiteList;
        
        /** CDタイトル */
        @SuppressWarnings("UnusedReturnValue")
        public CdInfoBuilder title(String title) {
            this.title = title;
            return this;
        }
        
        /** 品番 */
        @SuppressWarnings("UnusedReturnValue")
        public CdInfoBuilder recordNumbers(List<String> recordNumbers) {
            this.recordNumbers = recordNumbers;
            return this;
        }
        
        /** リリース日 */
        @SuppressWarnings("UnusedReturnValue")
        @JsonDeserialize(using = LocalDateDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        public CdInfoBuilder releaseDate(LocalDate releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }
        
        /** CD情報ページURL */
        @SuppressWarnings("UnusedReturnValue")
        public CdInfoBuilder cdInfoPageUrl(String cdInfoPageUrl) {
            this.cdInfoPageUrl = cdInfoPageUrl;
            return this;
        }
        
        /** CDジャケット画像URL */
        @SuppressWarnings("UnusedReturnValue")
        public CdInfoBuilder jacketUrl(String jacketUrl) {
            this.jacketUrl = jacketUrl;
            return this;
        }
        
        /** 限定販売か */
        @SuppressWarnings("UnusedReturnValue")
        public CdInfoBuilder limited(boolean limited) {
            this.limited = limited;
            return this;
        }
        
        /** シリーズ名 */
        @SuppressWarnings("UnusedReturnValue")
        public CdInfoBuilder series(String series) {
            this.series = series;
            return this;
        }
        
        /** アーティスト名 */
        @SuppressWarnings("UnusedReturnValue")
        public CdInfoBuilder artist(String artist) {
            this.artist = artist;
            return this;
        }
        
        /** ダウンロード・ストリーミングサイトリスト */
        @SuppressWarnings("UnusedReturnValue")
        public CdInfoBuilder downloadSiteList(List<StoreSite> downloadSiteList) {
            this.downloadSiteList = downloadSiteList;
            return this;
        }
        
        /** CD販売サイトリスト */
        @SuppressWarnings("UnusedReturnValue")
        public CdInfoBuilder purchaseSiteList(List<StoreSite> purchaseSiteList) {
            this.purchaseSiteList = purchaseSiteList;
            return this;
        }
        
        /** ビルド */
        public CdInfo build() {
            return new CdInfo(this);
        }
    }
}