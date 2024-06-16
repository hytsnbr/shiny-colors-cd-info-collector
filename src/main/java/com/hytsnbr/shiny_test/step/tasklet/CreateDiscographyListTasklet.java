package com.hytsnbr.shiny_test.step.tasklet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.hytsnbr.shiny_test.config.ApplicationConfig;
import com.hytsnbr.shiny_test.dto.DiscographyData;
import com.hytsnbr.shiny_test.exception.SystemException;
import com.hytsnbr.shiny_test.service.FileOperator;
import com.hytsnbr.shiny_test.util.JsoupUtil;

/** ディスコグラフィー情報リスト作成タスクレット */
@Component
public class CreateDiscographyListTasklet implements Tasklet {
    
    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(CreateDiscographyListTasklet.class);
    
    /** アプリ設定 */
    private final ApplicationConfig appConfig;
    
    private final FileOperator fileOperator;
    
    /** コンストラクタ */
    public CreateDiscographyListTasklet(ApplicationConfig appConfig, FileOperator fileOperator) {
        this.appConfig = appConfig;
        this.fileOperator = fileOperator;
    }
    
    @SuppressWarnings("NullableProblems")
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        var discographyList = this.createDiscographyList();
        
        this.fileOperator.outputToJsonFile(discographyList, "DiscographyList.json");
        
        return RepeatStatus.FINISHED;
    }
    
    private List<DiscographyData> createDiscographyList() {
        List<DiscographyData> discographyList = new ArrayList<>();
        
        // ランティスのシャニマスサイト（ディスコグラフィーページ）に接続
        Document document;
        try {
            document = JsoupUtil.connectJsoup(appConfig, appConfig.getTargetUrl());
        } catch (HttpStatusException e) {
            throw new SystemException("ディスコグラフィーページの取得に失敗しました");
        }
        
        // 上記ページのディスコグラフィー一覧を取得
        var discographySection = document.getElementById("discographys");
        if (Objects.isNull(discographySection)) {
            throw new SystemException("ディスコグラフィー情報がページ内に存在しません");
        }
        
        // ディスコグラフィーページ内に「CD」と「Blu-ray」のセクションがあるので「CD」の方だけ取得
        var cdArea = discographySection.getElementsByClass("CD_area").getFirst();
        // CDシリーズをリストで取得
        var dscBox = cdArea.getElementsByClass("dsc_box");
        for (var dscBoxElement : dscBox) {
            var seriesName = dscBoxElement.select("h4 > span").text().replace("■", "");
            logger.info("Series Name: {}", seriesName);
            
            // CDシリーズ内のCD一覧を取得
            var dscList = dscBoxElement.select("a");
            for (var dsc : dscList) {
                var cdDetailUrl = dsc.attr("href");
                logger.info("CD Detail URL: {}", cdDetailUrl);
                
                var discographyData = DiscographyData.of(cdDetailUrl, seriesName);
                
                discographyList.add(discographyData);
            }
        }
        
        return discographyList;
    }
}
