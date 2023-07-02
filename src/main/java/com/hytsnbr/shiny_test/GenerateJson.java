package com.hytsnbr.shiny_test;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hytsnbr.shiny_test.constant.Store;
import com.hytsnbr.shiny_test.dto.JsonData;
import com.hytsnbr.shiny_test.dto.JsonData.CDInfo;
import com.hytsnbr.shiny_test.dto.JsonData.StoreSite;
import com.hytsnbr.shiny_test.exception.SystemException;

public class GenerateJson {
    
    private static final String URL = "https://shinycolors.lantis.jp/discography/";
    
    private static final String JSON_FILE_PATH = "result/data.json";
    
    private static final DateTimeFormatter releaseDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d");
    
    private static final ObjectMapper objectMapper;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateJson.class);
    
    /**
     * staticイニシャライザー
     */
    static {
        objectMapper = new ObjectMapper();
        // Jacksonで Java8 の LocalDate 関係を処理できるようにする
        objectMapper.registerModule(new JavaTimeModule());
    }
    
    public void generate() {
        // 前回作成したファイルの作成日チェック
        if (this.isCreationDateToday()) {
            LOGGER.info("前回のファイル作成処理から日付が変わっていないため処理は中止されました");
            
            return;
        }
        
        List<CDInfo> cdInfoList = new ArrayList<>();
        var document = this.connectJsoup(URL);
        
        var discographySection = document.getElementById("discographys");
        if (Objects.isNull(discographySection)) {
            throw new SystemException("ディスコグラフィー情報がページ内に存在しません");
        }
        
        var cdArea = discographySection.getElementsByClass("CD_area").get(0);
        var dscBox = cdArea.getElementsByClass("dsc_box");
        for (var dscBoxElement : dscBox) {
            var dscList = dscBoxElement.select("a");
            for (var dsc : dscList) {
                var cdInfo = new CDInfo();
                
                var detailPage = this.connectJsoup(dsc.attr("href"));
                var releaseSection = detailPage.getElementById("release");
                if (Objects.isNull(releaseSection)) {
                    throw new SystemException("CD情報がページ内に存在しません");
                }
                
                var releaseImg = releaseSection.select(".release_box > .release_img > img").attr("src");
                cdInfo.setJacketUrl(releaseImg);
                LOGGER.debug("Jacket Image: {}", cdInfo.getJacketUrl());
                
                var titleBox = releaseSection.getElementsByClass("titles").get(0);
                var title = titleBox.getElementsByTag("h2").text();
                LOGGER.info("CD Name: {}", title);
                LOGGER.debug("CD URL: {}", dsc.attr("href"));
                cdInfo.setTitle(title);
                
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
                        cdInfo.setArtist(artistText);
                    }
                    
                    // 品番
                    if (StringUtils.contains(childNode.toString(), "品番：")) {
                        var recordNumber = childNode.toString().replaceAll("[\r\n]", "")
                                                    .trim().replace("品番：", "");
                        LOGGER.debug("CD Record Number: {}", recordNumber);
                        cdInfo.setRecordNumber(recordNumber);
                    }
                }
                
                // リリース日
                var releaseDateText = infoTexts.get(1).childNode(0).toString().replace("発売日：", "");
                LOGGER.debug("CD Release Date: {}", releaseDateText);
                if (StringUtils.isNotBlank(releaseDateText)) {
                    try {
                        cdInfo.setReleaseDate(LocalDate.parse(releaseDateText, releaseDateTimeFormatter));
                    } catch (DateTimeParseException e) {
                        throw new SystemException("リリース日：日付変換に失敗しました");
                    }
                }
                
                // 品番（たまに違う位置にある場合の対応）
                try {
                    var recordNumber = infoTexts.get(1).childNode(2).toString().replaceAll("[\r\n]", "")
                                                .trim().replace("品番：", "");
                    LOGGER.debug("CD Record Number: {}", recordNumber);
                    cdInfo.setRecordNumber(recordNumber);
                } catch (IndexOutOfBoundsException e) {
                    // NOTE: イレギュラーでない場合は例外が発生するが正常なので何もしない
                }
                
                var storeLinkPageList = infoTexts.select(".shopbanc > .banshopint");
                // ショップサイトのリンクが無い場合は限定販売とする
                if (storeLinkPageList.isEmpty()) {
                    cdInfo.setLimited(true);
                    cdInfoList.add(cdInfo);
                    
                    continue;
                }
                
                // ダウンロードサイト & CDショップサイト URL取得
                // 片方だけの場合にも対応
                String downloadSiteLinkPageUrl = "";
                String shopSiteLinkPageUrl = "";
                for (Element storeLinkPage : storeLinkPageList) {
                    if (StringUtils.equals(storeLinkPage.childNode(0).attr("alt"), "配信サイトで購入")) {
                        downloadSiteLinkPageUrl = storeLinkPage.attr("href");
                    }
                    if (StringUtils.equals(storeLinkPage.childNode(0).attr("alt"), "ストアで購入")) {
                        shopSiteLinkPageUrl = storeLinkPage.attr("href");
                    }
                }
                
                // ダウンロードサイトリスト
                LOGGER.debug("ダウンロードサイト");
                List<StoreSite> downloadSiteList = this.getStoreData(downloadSiteLinkPageUrl);
                cdInfo.setDownloadSiteList(downloadSiteList);
                
                // CDショップサイト
                LOGGER.debug("ショップサイト");
                List<StoreSite> purchaseSiteList = this.getStoreData(shopSiteLinkPageUrl);
                cdInfo.setPurchaseSiteList(purchaseSiteList);
                
                cdInfoList.add(cdInfo);
            }
        }
        
        // 前回データ一致する場合はファイル出力しない
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
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new SystemException(e);
        }
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
        for (Element e : siteLinkList) {
            var siteUrl = e.attr("href");
            LOGGER.debug("{}: {}", e.attr("data-label"), siteUrl);
            Store store = Store.getByDomain(siteUrl);
            StoreSite storeSite = new StoreSite(store.getStoreName(), siteUrl);
            siteList.add(storeSite);
        }
        
        return siteList;
    }
    
    /**
     * JSONファイルに出力
     */
    private void outputToJsonFile(List<CDInfo> cdInfoList) {
        Path path = Paths.get(JSON_FILE_PATH);
        try {
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new SystemException(e);
        }
        
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            String jsonText = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(JsonData.of(cdInfoList));
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
        
        final var today = LocalDate.now().toEpochDay();
        return data.getCreatedAt() == today;
    }
    
    /**
     * JSONファイルを読み取ってオブジェクトで返却する
     */
    private JsonData readJsonFile() {
        try {
            return objectMapper.readValue(Paths.get(JSON_FILE_PATH).toFile(), JsonData.class);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new SystemException(e);
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
        
        for (CDInfo cdInfo : cdInfoList) {
            if (!data.getCdInfoList().contains(cdInfo)) {
                return false;
            }
        }
        
        return true;
    }
}
