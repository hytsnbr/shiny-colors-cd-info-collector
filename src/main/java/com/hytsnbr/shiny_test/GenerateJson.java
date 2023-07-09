package com.hytsnbr.shiny_test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hytsnbr.shiny_test.config.ApplicationConfig;
import com.hytsnbr.shiny_test.constant.Store;
import com.hytsnbr.shiny_test.dto.CDInfo;
import com.hytsnbr.shiny_test.dto.JsonData;
import com.hytsnbr.shiny_test.dto.StoreSite;
import com.hytsnbr.shiny_test.exception.SystemException;

@Component
public class GenerateJson {
    
    private static final ObjectMapper objectMapper;
    
    private static final DateTimeFormatter releaseDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d");
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateJson.class);
    
    private final ApplicationConfig appConfig;
    
    /* staticイニシャライザー */
    static {
        objectMapper = new ObjectMapper();
        // Jacksonで Java8 の LocalDate 関係を処理できるようにする
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    /** コンストラクタ */
    public GenerateJson(ApplicationConfig appConfig) {
        this.appConfig = appConfig;
    }
    
    /**
     * 生成処理
     */
    public void generate() {
        // 前回作成したファイルの作成日チェック
        if (this.isCreationDateToday()) {
            LOGGER.info("前回のファイル作成処理から日付が変わっていないため処理は中止されました");
            
            return;
        }
        
        List<CDInfo> cdInfoList = new ArrayList<>();
        var document = this.connectJsoup(this.appConfig.getTargetUrl());
        
        var discographySection = document.getElementById("discographys");
        if (Objects.isNull(discographySection)) {
            throw new SystemException("ディスコグラフィー情報がページ内に存在しません");
        }
        
        var cdArea = discographySection.getElementsByClass("CD_area").get(0);
        var dscBox = cdArea.getElementsByClass("dsc_box");
        for (var dscBoxElement : dscBox) {
            var seriesName = dscBoxElement.select("h4 > span").text().replace("■", "");
            LOGGER.info("Series Name: {}", seriesName);
            
            var dscList = dscBoxElement.select("a");
            for (var dsc : dscList) {
                var cdInfoBuilder = CDInfo.builder();
                cdInfoBuilder.series(seriesName);
                
                var detailPage = this.connectJsoup(dsc.attr("href"));
                var releaseSection = detailPage.getElementById("release");
                if (Objects.isNull(releaseSection)) {
                    throw new SystemException("CD情報がページ内に存在しません");
                }
                
                var releaseImg = releaseSection.select(".release_box > .release_img > img").attr("src");
                cdInfoBuilder.jacketUrl(releaseImg);
                LOGGER.debug("Jacket Image: {}", releaseImg);
                
                var titleBox = releaseSection.getElementsByClass("titles").get(0);
                var title = titleBox.getElementsByTag("h2").text();
                LOGGER.info("CD Name: {}", title);
                LOGGER.debug("CD URL: {}", dsc.attr("href"));
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
                        LOGGER.debug("CD Artist Name: {}", artistText);
                        cdInfoBuilder.artist(artistText);
                    }
                    
                    // 品番（表記の関係で違う位置にある場合）
                    if (StringUtils.contains(childNode.toString(), "品番：")) {
                        var recordNumberText = childNode.toString().replaceAll("[\r\n]", "")
                                                        .trim().replace("品番：", "");
                        LOGGER.debug("CD Record Number: {}", recordNumberText);
                        cdInfoBuilder.recordNumbers(this.getRecordNumberList(recordNumberText));
                    }
                }
                
                // リリース日
                var releaseDateText = infoTexts.get(1).childNode(0).toString().replace("発売日：", "");
                LOGGER.debug("CD Release Date: {}", releaseDateText);
                if (StringUtils.isNotBlank(releaseDateText)) {
                    try {
                        cdInfoBuilder.releaseDate(LocalDate.parse(releaseDateText, releaseDateTimeFormatter));
                    } catch (DateTimeParseException e) {
                        throw new SystemException("リリース日：日付変換に失敗しました", e);
                    }
                }
                
                // 品番（通常位置に記載されている場合）
                try {
                    var recordNumberText = infoTexts.get(1).childNode(2).toString().replaceAll("[\r\n]", "")
                                                    .trim().replace("品番：", "");
                    LOGGER.debug("CD Record Number: {}", recordNumberText);
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
                LOGGER.debug("ダウンロードサイト");
                var downloadSiteList = this.getStoreData(downloadSiteLinkPageUrl);
                cdInfoBuilder.downloadSiteList(downloadSiteList);
                
                // CDショップサイト
                LOGGER.debug("ショップサイト");
                var purchaseSiteList = this.getStoreData(shopSiteLinkPageUrl);
                cdInfoBuilder.purchaseSiteList(purchaseSiteList);
                
                cdInfoList.add(cdInfoBuilder.build());
            }
        }
        
        // 前回処理後のデータと一致する場合はファイル出力しない
        if (!this.matchPrevCDInfoList(cdInfoList)) {
            this.outputToJsonFile(cdInfoList);
            
            LOGGER.info("ファイルを作成しました");
        } else {
            LOGGER.info("前回処理時とデータ内容に変化がないためファイルは作成されませんでした");
        }
    }
    
    /**
     * ページ取得
     *
     * @param url ページURL
     */
    private Document connectJsoup(String url) {
        try {
            var connection = Jsoup.connect(url);
            connection.timeout(appConfig.getJsoup().getTimeout());
            
            return connection.get();
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
     * @param url ストアページURL
     */
    private List<StoreSite> getStoreData(String url) {
        if (StringUtils.isBlank(url)) {
            return Collections.emptyList();
        }
        
        var siteLinkPage = this.connectJsoup(url);
        var siteLinkList = siteLinkPage.select(".music-service-list__item > a");
        List<StoreSite> siteList = new ArrayList<>();
        for (var e : siteLinkList) {
            // TODO: ハイレゾ対応可否を確認する処理
            var siteUrl = e.attr("href");
            LOGGER.debug("{}: {}", e.attr("data-label"), siteUrl);
            var storeEnum = Store.getByDomain(siteUrl);
            var storeSite = new StoreSite(storeEnum, siteUrl);
            siteList.add(storeSite);
        }
        
        return siteList;
    }
    
    /**
     * JSONファイルに出力
     */
    private void outputToJsonFile(List<CDInfo> cdInfoList) {
        var path = Paths.get(this.appConfig.getJsonPath());
        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
        } catch (IOException e) {
            throw new SystemException("ファイル作成に失敗しました", e);
        }
        
        try (var bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            var prettyPrinter = new DefaultPrettyPrinter();
            prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
            var jsonText = objectMapper.writer(prettyPrinter).writeValueAsString(JsonData.of(cdInfoList));
            bufferedWriter.write(jsonText);
        } catch (Exception e) {
            throw new SystemException(e);
        }
    }
    
    /**
     * 作成済みのファイルの作成日が現在日と一致するか<br>
     * 一致する場合：<code>true</code><br>
     * 一致しない場合、ファイルが存在しない・読み込みに失敗した場合：<code>false</code>
     */
    private boolean isCreationDateToday() {
        var data = this.readJsonFile();
        if (Objects.isNull(data)) {
            return false;
        }
        
        final var createdAt = LocalDate.ofEpochDay(data.getCreatedAt());
        final var today = LocalDate.now();
        LOGGER.info("ファイル作成日: {}", createdAt);
        LOGGER.info("        処理日: {}", today);
        
        return data.getCreatedAt() == today.toEpochDay();
    }
    
    /**
     * JSONファイルを読み取ってオブジェクトで返却する
     */
    private JsonData readJsonFile() {
        try {
            var jsonFile = Paths.get(this.appConfig.getJsonPath()).toFile();
            return objectMapper.readValue(jsonFile, JsonData.class);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new SystemException("生成ファイルの読み込みに失敗しました", e);
        }
    }
    
    /**
     * 前回処理時のデータと一致するか
     *
     * @param cdInfoList CD情報リスト
     *
     * @return 一致する場合は true
     */
    private boolean matchPrevCDInfoList(List<CDInfo> cdInfoList) {
        var data = this.readJsonFile();
        if (Objects.isNull(data)) {
            return false;
        }
        
        for (var cdInfo : cdInfoList) {
            if (!data.getCdInfoList().contains(cdInfo)) {
                return false;
            }
        }
        
        return true;
    }
}
