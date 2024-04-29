package com.hytsnbr.shiny_test.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
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
    
    /** 発売日取得時の日付フォーマット */
    private static final DateTimeFormatter releaseDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d");
    
    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(GenerateJson.class);
    
    /** アプリ設定 */
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
        
        // ランティスのシャニマスサイト（ディスコグラフィーページ）に接続
        Document document;
        try {
            document = this.connectJsoup(this.appConfig.getTargetUrl());
        } catch (HttpStatusException e) {
            throw new SystemException("ディスコグラフィーページの取得に失敗しました");
        }
        
        // 上記ページのディスコグラフィー一覧を取得
        var discographySection = document.getElementById("discographys");
        if (Objects.isNull(discographySection)) {
            throw new SystemException("ディスコグラフィー情報がページ内に存在しません");
        }
        
        // ディスコグラフィーページ内に「CD」と「Blu-ray」のセクションがあるので「CD」の方だけ取得
        var cdArea = discographySection.getElementsByClass("CD_area").get(0);
        // CDシリーズをリストで取得
        var dscBox = cdArea.getElementsByClass("dsc_box");
        for (var dscBoxElement : dscBox) {
            var seriesName = dscBoxElement.select("h4 > span").text().replace("■", "");
            logger.info("Series Name: {}", seriesName);
            
            // CDシリーズ内のCD一覧を取得
            var dscList = dscBoxElement.select("a");
            for (var dsc : dscList) {
                var cdInfoBuilder = CdInfo.builder();
                cdInfoBuilder.series(seriesName);
                
                // CD詳細ページに接続
                Document detailPage;
                try {
                    detailPage = this.connectJsoup(dsc.attr("href"));
                } catch (HttpStatusException e) {
                    throw new SystemException("CD詳細ページの取得に失敗しました");
                }
                
                // CD情報を取得
                var releaseSection = detailPage.getElementById("release");
                if (Objects.isNull(releaseSection)) {
                    throw new SystemException("CD情報がページ内に存在しません");
                }
                
                // CDジャケット画像のURLを取得
                var releaseImg = releaseSection.select(".release_box > .release_img > img").attr("src");
                cdInfoBuilder.jacketUrl(releaseImg);
                logger.debug("Jacket Image: {}", releaseImg);
                
                // CDタイトルを取得
                var titleBox = releaseSection.getElementsByClass("titles").get(0);
                var title = titleBox.getElementsByTag("h2").text();
                logger.info("CD Name: {}", title);
                logger.debug("CD URL: {}", dsc.attr("href"));
                cdInfoBuilder.title(title);
                
                var releaseContentsBox = releaseSection.getElementsByClass("release_contents").get(0);
                var infoTexts = releaseContentsBox.children();
                for (var element : infoTexts) {
                    for (var childNode : element.childNodes()) {
                        if (!(childNode instanceof TextNode)) {
                            continue;
                        }
                        
                        // アーティスト名を取得
                        this.getArtistName(cdInfoBuilder, (TextNode) childNode);
                        
                        // 品番を取得
                        this.getRecordNumber(cdInfoBuilder, (TextNode) childNode);
                        
                        // 発売日を取得
                        this.getReleaseDate(cdInfoBuilder, (TextNode) childNode);
                    }
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
                
                logger.debug("====================================================================================");
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
     * アーティスト名を取得する
     */
    private void getArtistName(CdInfo.CdInfoBuilder cdInfoBuilder, TextNode node) {
        if (!StringUtils.contains(node.toString(), "アーティスト：")) {
            return;
        }
        
        // アーティスト：シャイニーカラーズ の形式なのでアーティスト名だけ取り出し
        var artistText = node.toString().replace("アーティスト：", "");
        logger.debug("CD Artist Name: {}", artistText);
        cdInfoBuilder.artist(artistText);
    }
    
    /**
     * 品番を取得する
     */
    private void getRecordNumber(CdInfo.CdInfoBuilder cdInfoBuilder, TextNode node) {
        if (!StringUtils.contains(node.toString(), "品番：")) {
            return;
        }
        
        // 品番：LACM-14781 の形式なので品番だけ取り出し
        // 「品番：」の前に改行？スペース？があるので処理を入れる
        var recordNumberText = node.toString()
                                   .replaceAll("[\r\n]", "")
                                   .trim()
                                   .replace("品番：", "");
        logger.debug("CD Record Number: {}", recordNumberText);
        // 複数枚の場合を考慮して品番リスト取得処理を噛ませる
        cdInfoBuilder.recordNumbers(this.getRecordNumberList(recordNumberText));
    }
    
    /**
     * 発売日を取得する
     */
    private void getReleaseDate(CdInfo.CdInfoBuilder cdInfoBuilder, TextNode node) {
        if (!StringUtils.contains(node.toString(), "発売日：")) {
            return;
        }
        
        // 発売日：2018/6/6 の形式なので日付だけ取り出し
        var releaseDateText = node.toString().replace("発売日：", "");
        logger.debug("CD Release Date: {}", releaseDateText);
        if (StringUtils.isNotBlank(releaseDateText)) {
            try {
                // java.time.LocalDate に変換
                cdInfoBuilder.releaseDate(LocalDate.parse(releaseDateText, releaseDateTimeFormatter));
            } catch (DateTimeParseException e) {
                throw new SystemException("リリース日：日付変換に失敗しました", e);
            }
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
        
        // 例: LACM-14781, LACA-9946～7
        var recordNumberList = new ArrayList<String>();
        // 「～」で分割（例: ["LACM-14781"] もしくは ["LACA-9946", "7"]
        var recordNumberTexts = recordNumberText.split("～");
        // 分割した配列長が1なら処理は終わり
        if (recordNumberTexts.length == 1) {
            return List.of(recordNumberText);
        }
        
        // 記号部分とシリアル番号に分割
        final var recordNumberTextList = recordNumberTexts[0].split("-");
        final var code = recordNumberTextList[0]; // 記号番号
        var number = Integer.parseInt(recordNumberTextList[1]); // シリアル番号
        final var numberText = String.valueOf(number);
        // 複数枚の最終ディスクのシリアル番号を表す番号を取得する
        // 例: LACA-9946～7 の場合は「7」、LACZ-10199～10200 の場合は「10200」
        final var lastIndex = recordNumberTexts.length - 1; // 番号
        final var lastIndexDigits = recordNumberTexts[lastIndex].length(); // 番号の桁数
        try {
            for (
                // 例: LACA-9946～7 の場合は最終ディスクは「7」で桁数が「1」なのでループを以下にする
                // 開始値: 6、終了値: 7
                // 例: LACZ-10199～10200 の場合は最終ディスクは「10200」で桁数が「5」なのでループを以下にする
                // 開始値: 10199、終了値: 10200
                var i = Integer.parseInt(numberText.substring(numberText.length() - lastIndexDigits));
                i <= Integer.parseInt(recordNumberTexts[lastIndex]);
                i++
            ) {
                // 「番号」+「シリアル番号」に組み立てる
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
        final var retryLimit = this.appConfig.getProcess().getRetryLimit();
        
        if (StringUtils.isBlank(url)) {
            return Collections.emptyList();
        }
        
        var tryCount = 1;
        Document siteLinkPage = null;
        while (tryCount <= retryLimit) {
            try {
                siteLinkPage = this.connectJsoup(url);
                
                break;
            } catch (HttpStatusException e) {
                logger.info("ストア一覧ページの取得に失敗しました（試行回数 {}/{}）：{}", tryCount, retryLimit, url);
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
        for (var i = 1; i <= retryLimit; i++) {
            for (var siteLink : siteLinkList) {
                try {
                    var store = Store.getByHtmlName(
                        siteLink.select(".music-service-list__content > img")
                                .attr("alt")
                    );
                    var name = store.getName();
                    var siteUrl = this.getNormalizationSiteUrl(new URL(siteLink.attr("href")), store);
                    var isHiRes = StringUtils.equals(
                        siteLink.select(".btn.music-service-list__btn").text(),
                        "ハイレゾDL"
                    );
                    var storeSite = new StoreSite(name, siteUrl, isHiRes);
                    logger.debug("{}: {}", name, siteUrl);
                    siteList.add(storeSite);
                } catch (MalformedURLException e) {
                    throw new SystemException(e);
                }
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
     * サイトURLから不要なクエリパラメータを削除して正規化する
     *
     * @param siteUrl 正規化前のサイトURL
     * @param store   サイトURLの属するサイト情報
     *
     * @return 正規化後のサイトURL
     */
    private String getNormalizationSiteUrl(URL siteUrl, Store store) {
        List<String> queryParams = List.of();
        if (StringUtils.isNoneEmpty(siteUrl.getQuery())) {
            queryParams = Arrays
                .stream(siteUrl.getQuery().split("&"))
                .filter(queryParam -> {
                    var key = queryParam.split("=")[0];
                    return store.getIncludeQueryParams().contains(key);
                }).toList();
        }
        
        return siteUrl.getProtocol() + "://" + siteUrl.getHost() + siteUrl.getPath()
            + (!queryParams.isEmpty() ? "?" + StringUtils.join(queryParams, "&") : "");
    }
    
    /**
     * クールタイム
     */
    private void coolTime() {
        try {
            Thread.sleep(this.appConfig.getProcess().getCoolTime());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            
            throw new SystemException(e);
        }
    }
}
