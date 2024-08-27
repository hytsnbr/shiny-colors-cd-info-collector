package com.hytsnbr.shiny_colors.step.chunk;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.hytsnbr.shiny_colors.config.ApplicationConfig;
import com.hytsnbr.shiny_colors.constant.Store;
import com.hytsnbr.shiny_colors.dto.CdInfo;
import com.hytsnbr.shiny_colors.dto.DiscographyData;
import com.hytsnbr.shiny_colors.dto.StoreSite;
import com.hytsnbr.shiny_colors.exception.CdInfoWebScrapingException;
import com.hytsnbr.shiny_colors.util.JsoupUtil;

/** CD情報データ作成プロセッサ */
@Component
public class CdInfoDataProcessor implements ItemProcessor<DiscographyData, CdInfo> {
    
    /** 発売日取得時の日付フォーマット */
    private static final DateTimeFormatter releaseDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d");
    
    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(CdInfoDataProcessor.class);
    
    /** アプリ設定 */
    private final ApplicationConfig appConfig;
    
    /** コンストラクタ */
    public CdInfoDataProcessor(ApplicationConfig appConfig) {
        this.appConfig = appConfig;
    }
    
    @SuppressWarnings("NullableProblems")
    @Override
    public CdInfo process(DiscographyData item) throws Exception {
        return createCdInfo(item);
    }
    
    /**
     * CD情報作成
     *
     * @param discographyData ディスコグラフィー情報
     *
     * @return CD情報
     *
     * @throws CdInfoWebScrapingException スクレイピング中にエラーが発生した場合
     */
    private CdInfo createCdInfo(final DiscographyData discographyData) throws CdInfoWebScrapingException {
        var cdInfoBuilder = CdInfo.builder();
        
        final var cdDetailUrl = discographyData.getUrl();
        cdInfoBuilder.cdInfoPageUrl(cdDetailUrl);
        cdInfoBuilder.series(discographyData.getSeriesName());
        
        // CD詳細ページに接続
        Document detailPage;
        try {
            detailPage = JsoupUtil.connectJsoup(appConfig, cdDetailUrl);
        } catch (HttpStatusException e) {
            throw new CdInfoWebScrapingException("CD詳細ページの取得に失敗しました");
        }
        
        // CD情報を取得
        var releaseSection = detailPage.getElementById("release");
        if (Objects.isNull(releaseSection)) {
            throw new CdInfoWebScrapingException("CD情報がページ内に存在しません");
        }
        
        // CDタイトルを取得
        var titleBox = releaseSection.getElementsByClass("titles").getFirst();
        var title = titleBox.getElementsByTag("h2").text();
        logger.info("CD Name: {}", title);
        cdInfoBuilder.title(title);
        
        logger.info("CD URL: {}", cdDetailUrl);
        logger.info("Series Name: {}", discographyData.getSeriesName());
        
        // CDジャケット画像のURLを取得
        var releaseImg = releaseSection.select(".release_box > .release_img > img").attr("src");
        cdInfoBuilder.jacketUrl(releaseImg);
        logger.info("Jacket Image: {}", releaseImg);
        
        var releaseContentsBox = releaseSection.getElementsByClass("release_contents").getFirst();
        var infoTexts = releaseContentsBox.children();
        
        String artistName;
        List<String> recordNumbers = new ArrayList<>();
        LocalDate releaseDate;
        for (var element : infoTexts) {
            for (var childNode : element.childNodes()) {
                if (!(childNode instanceof TextNode textNode)) {
                    continue;
                }
                
                // アーティスト名を取得
                if (StringUtils.contains(textNode.toString(), "アーティスト：")) {
                    artistName = this.getArtistName((TextNode) childNode);
                    cdInfoBuilder.artist(artistName);
                }
                
                // 品番を取得
                if (StringUtils.contains(textNode.toString(), "品番：")) {
                    recordNumbers = this.getRecordNumbers((TextNode) childNode);
                    cdInfoBuilder.recordNumbers(recordNumbers);
                }
                
                // 発売日を取得
                if (StringUtils.contains(textNode.toString(), "発売日：")) {
                    releaseDate = this.getReleaseDate((TextNode) childNode);
                    cdInfoBuilder.releaseDate(releaseDate);
                }
            }
        }
        
        var storeLinkPageList = infoTexts.select(".shopbanc > .banshopint");
        // ショップサイトのリンクが無い場合は限定販売とする
        if (storeLinkPageList.isEmpty()) {
            cdInfoBuilder.limited(true);
            
            return cdInfoBuilder.build();
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
        logger.info("ダウンロードサイト");
        var downloadSiteList = this.getStoreData(downloadSiteLinkPageUrl);
        // CD詳細ページにダウンロードサイトリストのURLが載っていない場合
        if (downloadSiteList.isEmpty()) {
            downloadSiteLinkPageUrl = "https://lnk.to/%ss".formatted(recordNumbers.getFirst());
            downloadSiteList = this.getStoreData(downloadSiteLinkPageUrl);
        }
        cdInfoBuilder.downloadSiteList(downloadSiteList);
        
        // CDショップサイト
        logger.info("ショップサイト");
        var purchaseSiteList = this.getStoreData(shopSiteLinkPageUrl);
        // CD詳細ページにショップサイトリストのURLが載っていない場合
        if (purchaseSiteList.isEmpty()) {
            shopSiteLinkPageUrl = "https://lnk.to/%sc".formatted(recordNumbers.getFirst());
            purchaseSiteList = this.getStoreData(shopSiteLinkPageUrl);
        }
        cdInfoBuilder.purchaseSiteList(purchaseSiteList);
        
        // 処理終了後クールタイム
        this.coolTime();
        
        return cdInfoBuilder.build();
    }
    
    /**
     * アーティスト名を取得する
     */
    private String getArtistName(TextNode node) throws CdInfoWebScrapingException {
        if (!StringUtils.contains(node.toString(), "アーティスト：")) {
            throw new CdInfoWebScrapingException("CD情報ページからアーティスト名が欠落しています");
        }
        
        // アーティスト：シャイニーカラーズ の形式なのでアーティスト名だけ取り出し
        var artistText = node.toString().replace("アーティスト：", "");
        logger.info("CD Artist Name: {}", artistText);
        
        return artistText;
    }
    
    /**
     * 品番を取得する
     */
    private List<String> getRecordNumbers(TextNode node) throws CdInfoWebScrapingException {
        if (!StringUtils.contains(node.toString(), "品番：")) {
            throw new CdInfoWebScrapingException("CD情報ページから品番が欠落しています");
        }
        
        // 品番：LACM-14781 の形式なので品番だけ取り出し
        // 「品番：」の前に改行？スペース？があるので処理を入れる
        var recordNumberText = node.toString()
                                   .replaceAll("[\r\n]", "")
                                   .trim()
                                   .replace("品番：", "");
        logger.info("CD Record Number: {}", recordNumberText);
        
        // 複数枚の場合を考慮して品番リスト取得処理を噛ませる
        return this.getRecordNumberList(recordNumberText);
    }
    
    /**
     * 発売日を取得する
     */
    private LocalDate getReleaseDate(TextNode node) throws CdInfoWebScrapingException {
        if (!StringUtils.contains(node.toString(), "発売日：")) {
            throw new CdInfoWebScrapingException("CD情報ページから発売日が欠落しています");
        }
        
        // 発売日：2018/6/6 の形式なので日付だけ取り出し
        var releaseDateText = node.toString().replace("発売日：", "");
        logger.info("CD Release Date: {}", releaseDateText);
        LocalDate releaseDate = null;
        if (StringUtils.isNotBlank(releaseDateText)) {
            try {
                // java.time.LocalDate に変換
                releaseDate = LocalDate.parse(releaseDateText, releaseDateTimeFormatter);
            } catch (DateTimeParseException e) {
                throw new CdInfoWebScrapingException("リリース日：日付変換に失敗しました", e);
            }
        }
        
        return releaseDate;
    }
    
    /**
     * 品番リストを取得する
     *
     * @param recordNumberText 品番テキスト
     *
     * @return 品番リスト
     */
    private List<String> getRecordNumberList(String recordNumberText) throws CdInfoWebScrapingException {
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
            throw new CdInfoWebScrapingException("品番リストの作成に失敗しました", e);
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
    private List<StoreSite> getStoreData(String url) throws CdInfoWebScrapingException {
        logger.info("処理対象ページURL：{}", url);
        
        // ページ取得時リトライ上限
        final var retryLimit = this.appConfig.getProcess().getRetryLimit();
        
        if (StringUtils.isBlank(url)) {
            return Collections.emptyList();
        }
        
        var tryCount = 1;
        Document siteLinkPage = null;
        while (tryCount <= retryLimit) {
            try {
                siteLinkPage = JsoupUtil.connectJsoup(appConfig, url);
                
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
                var store = Store.getByHtmlName(
                    siteLink.select(".music-service-list__content > img")
                            .attr("alt")
                );
                var name = store.getName();
                var siteUrl = this.getNormalizationStoreSiteUrl(siteLink.attr("href"), store);
                var isHiRes = StringUtils.equals(
                    siteLink.select(".btn.music-service-list__btn").text(),
                    "ハイレゾDL"
                );
                var storeSite = StoreSite.builder()
                                         .name(name)
                                         .url(siteUrl)
                                         .isHiRes(isHiRes)
                                         .build();
                logger.info("{}: {}", name, siteUrl);
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
     * ストアサイトURLから不要なクエリパラメータを削除して正規化する
     *
     * @param siteUrl 正規化前のストアサイトURL
     * @param store   ストアサイトURLの属するサイト情報
     *
     * @return 正規化後のストアサイトURL
     */
    private String getNormalizationStoreSiteUrl(String siteUrl, Store store) throws CdInfoWebScrapingException {
        URL url;
        try {
            url = new URI(siteUrl).toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new CdInfoWebScrapingException("ストアサイトURLの解析に失敗しました", e);
        }
        
        List<String> queryParams = List.of();
        if (StringUtils.isNoneEmpty(url.getQuery())) {
            queryParams = Arrays
                .stream(url.getQuery().split("&"))
                .filter(queryParam -> {
                    var key = queryParam.split("=")[0];
                    return store.getIncludeQueryParams().contains(key);
                }).toList();
        }
        
        return url.getProtocol() + "://" + url.getHost() + url.getPath()
            + (!queryParams.isEmpty() ? "?" + StringUtils.join(queryParams, "&") : "");
    }
    
    /**
     * クールタイム
     */
    private void coolTime() throws CdInfoWebScrapingException {
        try {
            Thread.sleep(this.appConfig.getProcess().getCoolTime());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            
            throw new CdInfoWebScrapingException(e);
        }
    }
}
