package com.hytsnbr.shiny_test.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hytsnbr.shiny_test.config.ApplicationConfig;
import com.hytsnbr.shiny_test.constant.Store;
import com.hytsnbr.shiny_test.dto.CdInfo;
import com.hytsnbr.shiny_test.dto.StoreSite;
import com.hytsnbr.shiny_test.exception.SystemException;

/**
 * JSONデータ作成
 */
@Component
public class GenerateJson {
    
    private static final DateTimeFormatter releaseDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d");
    
    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(GenerateJson.class);
    
    private final ApplicationConfig appConfig;
    
    /** コンストラクタ */
    public GenerateJson(ApplicationConfig appConfig) {
        this.appConfig = appConfig;
    }
    
    /**
     * 生成処理
     */
    public List<CdInfo> createCdInfoList() {
        List<CdInfo> cdInfoList = new ArrayList<>();
        Document document;
        try {
            document = this.connectJsoup(this.appConfig.getTargetUrl());
        } catch (HttpStatusException e) {
            throw new SystemException("ディスコグラフィーページの取得に失敗しました");
        }
        
        var discographySection = document.getElementById("discographys");
        if (Objects.isNull(discographySection)) {
            throw new SystemException("ディスコグラフィー情報がページ内に存在しません");
        }
        
        var cdArea = discographySection.getElementsByClass("CD_area").get(0);
        var dscBox = cdArea.getElementsByClass("dsc_box");
        for (var dscBoxElement : dscBox) {
            var seriesName = dscBoxElement.select("h4 > span").text().replace("■", "");
            logger.info("Series Name: {}", seriesName);
            
            var dscList = dscBoxElement.select("a");
            for (var dsc : dscList) {
                var cdInfoBuilder = CdInfo.builder();
                cdInfoBuilder.series(seriesName);
                
                Document detailPage;
                try {
                    detailPage = this.connectJsoup(dsc.attr("href"));
                } catch (HttpStatusException e) {
                    throw new SystemException("CD詳細ページの取得に失敗しました");
                }
                var releaseSection = detailPage.getElementById("release");
                if (Objects.isNull(releaseSection)) {
                    throw new SystemException("CD情報がページ内に存在しません");
                }
                
                var releaseImg = releaseSection.select(".release_box > .release_img > img").attr("src");
                cdInfoBuilder.jacketUrl(releaseImg);
                logger.debug("Jacket Image: {}", releaseImg);
                
                var titleBox = releaseSection.getElementsByClass("titles").get(0);
                var title = titleBox.getElementsByTag("h2").text();
                logger.info("CD Name: {}", title);
                logger.debug("CD URL: {}", dsc.attr("href"));
                cdInfoBuilder.title(title);
                
                var releaseContentsBox = releaseSection.getElementsByClass("release_contents").get(0);
                var infoTexts = releaseContentsBox.children();
                
                for (Object childNode : infoTexts.get(0).childNodes()) {
                    if (!(childNode instanceof TextNode)) {
                        continue;
                    }
                    
                    // アーティスト名
                    if (StringUtils.contains(childNode.toString(), "アーティスト：")) {
                        var artistText = childNode.toString().replace("アーティスト：", "");
                        logger.debug("CD Artist Name: {}", artistText);
                        cdInfoBuilder.artist(artistText);
                    }
                    
                    // 品番（表記の関係で違う位置にある場合）
                    if (StringUtils.contains(childNode.toString(), "品番：")) {
                        var recordNumberText = childNode.toString().replaceAll("[\r\n]", "")
                                                        .trim().replace("品番：", "");
                        logger.debug("CD Record Number: {}", recordNumberText);
                        cdInfoBuilder.recordNumbers(this.getRecordNumberList(recordNumberText));
                    }
                }
                
                // リリース日
                if (StringUtils.contains(infoTexts.get(1).childNode(0).toString(), "発売日：")) {
                    var releaseDateText = infoTexts.get(1).childNode(0).toString().replace("発売日：", "");
                    logger.debug("CD Release Date: {}", releaseDateText);
                    if (StringUtils.isNotBlank(releaseDateText)) {
                        try {
                            cdInfoBuilder.releaseDate(LocalDate.parse(releaseDateText, releaseDateTimeFormatter));
                        } catch (DateTimeParseException e) {
                            throw new SystemException("リリース日：日付変換に失敗しました", e);
                        }
                    }
                }
                
                // 品番（通常位置に記載されている場合）
                try {
                    var recordNumberText = infoTexts.get(1).childNode(2).toString().replaceAll("[\r\n]", "")
                                                    .trim().replace("品番：", "");
                    logger.debug("CD Record Number: {}", recordNumberText);
                    cdInfoBuilder.recordNumbers(this.getRecordNumberList(recordNumberText));
                } catch (IndexOutOfBoundsException e) {
                    // NOTE: 表記の関係で通常位置に記載されていない場合は例外が発生するが正常なので何もしない
                }
                
                var storeLinkPageList = infoTexts.select(".shopbanc > .banshopint");
                // ショップサイトのリンクが無い場合は限定販売とする
                if (storeLinkPageList.isEmpty()) {
                    cdInfoBuilder.limited(true);
                    cdInfoList.add(cdInfoBuilder.build());
                    
                    continue;
                }
                
                // ダウンロードサイト & CDショップサイト URL取得
                // 片方だけの場合にも対応
                var downloadSiteLinkPageUrl = "";
                var shopSiteLinkPageUrl = "";
                for (var storeLinkPage : storeLinkPageList) {
                    if (StringUtils.equals(storeLinkPage.childNode(0).attr("alt"), "配信サイトで購入")) {
                        downloadSiteLinkPageUrl = storeLinkPage.attr("href");
                    }
                    if (StringUtils.equals(storeLinkPage.childNode(0).attr("alt"), "ストアで購入")) {
                        shopSiteLinkPageUrl = storeLinkPage.attr("href");
                    }
                }
                
                // ダウンロードサイトリスト
                logger.debug("ダウンロードサイト");
                var downloadSiteList = this.getStoreData(downloadSiteLinkPageUrl);
                cdInfoBuilder.downloadSiteList(downloadSiteList);
                // クールタイム
                this.coolTime();
                
                // CDショップサイト
                logger.debug("ショップサイト");
                var purchaseSiteList = this.getStoreData(shopSiteLinkPageUrl);
                cdInfoBuilder.purchaseSiteList(purchaseSiteList);
                // クールタイム
                this.coolTime();
                
                cdInfoList.add(cdInfoBuilder.build());
            }
        }
        
        return cdInfoList;
    }
    
    /**
     * ページ取得
     *
     * @param url ページURL
     *
     * @throws HttpStatusException ページ情報の取得に失敗した場合
     */
    private Document connectJsoup(String url) throws HttpStatusException {
        try {
            var connection = Jsoup.connect(url);
            connection.timeout(appConfig.getJsoup().getTimeout());
            
            return connection.get();
        } catch (HttpStatusException e) {
            throw e;
        } catch (IOException e) {
            throw new SystemException(e);
        }
    }
    
    /**
     * 品番リストを取得する
     *
     * @param recordNumberText 品番テキスト
     *
     * @return 品番リスト
     */
    private List<String> getRecordNumberList(String recordNumberText) {
        if (StringUtils.isBlank(recordNumberText)) {
            return Collections.emptyList();
        }
        
        var recordNumberList = new ArrayList<String>();
        var recordNumberTexts = recordNumberText.split("～");
        if (recordNumberTexts.length == 1) {
            return List.of(recordNumberText);
        }
        
        final var code = recordNumberTexts[0].split("-")[0];
        var number = Integer.parseInt(recordNumberTexts[0].split("-")[1]);
        final var numberText = String.valueOf(number);
        final var lastIndex = recordNumberTexts.length - 1;
        try {
            for (
                var i = Integer.parseInt(String.valueOf(numberText.charAt(numberText.length() - 1)));
                i <= Integer.parseInt(recordNumberTexts[lastIndex]);
                i++
            ) {
                recordNumberList.add(String.format("%s-%d", code, number));
                number++;
            }
        } catch (Exception e) {
            throw new SystemException("品番リストの作成に失敗しました", e);
        }
        
        return recordNumberList;
    }
    
    /**
     * ストアページ情報取得
     *
     * @param url ストア一覧ページURL
     *
     * @return ストア情報リスト<br>
     * ストア一覧ページURLが空の場合、ストア一覧ページ情報の取得に失敗した場合は空のリストを返却する
     */
    private List<StoreSite> getStoreData(String url) {
        // ページ取得時リトライ上限
        final var RETRY_LIMIT = 5;
        
        if (StringUtils.isBlank(url)) {
            return Collections.emptyList();
        }
        
        int tryCount = 1;
        Document siteLinkPage = null;
        while (tryCount <= RETRY_LIMIT) {
            try {
                siteLinkPage = this.connectJsoup(url);
                
                break;
            } catch (HttpStatusException e) {
                logger.info("ストア一覧ページの取得に失敗しました（試行回数 {}/{}）：{}", tryCount, RETRY_LIMIT, url);
            }
            tryCount++;
            
            // 再試行前クールタイム
            this.coolTime();
        }
        if (Objects.isNull(siteLinkPage)) {
            logger.info("ストア一覧ページの取得ができませんでした");
            
            return Collections.emptyList();
        }
        
        var siteLinkList = siteLinkPage.select(".music-service-list__item > a");
        List<StoreSite> siteList = new ArrayList<>();
        for (var i = 1; i <= RETRY_LIMIT; i++) {
            for (var e : siteLinkList) {
                var siteUrl = e.attr("href");
                var name = Store.getByHtmlName(e.select(".music-service-list__content > img").attr("alt")).getName();
                var isHiRes = StringUtils.equals(e.select(".btn.music-service-list__btn").text(), "ハイレゾDL");
                var storeSite = new StoreSite(name, siteUrl, isHiRes);
                logger.debug("{}: {}", name, siteUrl);
                siteList.add(storeSite);
            }
            
            // サイトリストが空でない場合は処理終了
            if (!siteList.isEmpty()) {
                break;
            }
            
            // 再試行前クールタイム
            this.coolTime();
        }
        
        return siteList;
    }
    
    /**
     * クールタイム
     */
    private void coolTime() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            
            throw new SystemException(e);
        }
    }
}
