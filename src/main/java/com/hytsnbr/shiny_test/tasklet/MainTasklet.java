package com.hytsnbr.shiny_test.tasklet;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hytsnbr.shiny_test.dto.CdInfo;
import com.hytsnbr.shiny_test.service.FileOperator;
import com.hytsnbr.shiny_test.service.GenerateJson;

@Component
public class MainTasklet implements Tasklet {
    
    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(MainTasklet.class);
    
    private final GenerateJson generateJson;
    
    private final FileOperator fileOperator;
    
    /** 強制更新 */
    @Value("${app-config.force}")
    private boolean isForce;
    
    /** コンストラクタ */
    public MainTasklet(GenerateJson generateJson, FileOperator fileOperator) {
        this.generateJson = generateJson;
        this.fileOperator = fileOperator;
    }
    
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        // 前回作成したファイルの作成日チェック
        if (this.isCreationDateToday()) {
            logger.info("前回のファイル作成処理から日付が変わっていないため処理は中止されました");
            
            return RepeatStatus.FINISHED;
        }
        
        var cdInfoList = this.generateJson.createCdInfoList();
        
        // 前回処理後のデータと一致する場合はファイル出力しない
        if (!this.matchPrevCdInfoList(cdInfoList)) {
            this.fileOperator.outputToJsonFile(cdInfoList);
            
            logger.info("ファイルを作成しました");
        } else {
            logger.info("前回処理時とデータ内容に変化がないためファイルは作成されませんでした");
        }
        
        return RepeatStatus.FINISHED;
    }
    
    /**
     * 作成済みのファイルの作成日が現在日と一致するか<br>
     * 一致する場合：<code>true</code><br>
     * 一致しない場合、ファイルが存在しない・読み込みに失敗した場合：<code>false</code>
     */
    private boolean isCreationDateToday() {
        if (this.isForce) return false;
        
        var data = this.fileOperator.readJsonFile();
        if (Objects.isNull(data)) {
            return false;
        }
        
        final var createdAt = LocalDate.ofEpochDay(data.getCreatedAt());
        final var today = LocalDate.now();
        logger.info("ファイル作成日: {}", createdAt);
        logger.info("処理日: {}", today);
        
        return data.getCreatedAt() == today.toEpochDay();
    }
    
    /**
     * 前回処理時のデータと一致するか
     *
     * @param cdInfoList CD情報リスト
     *
     * @return 一致する場合は true
     */
    private boolean matchPrevCdInfoList(List<CdInfo> cdInfoList) {
        if (this.isForce) return false;
        
        var data = this.fileOperator.readJsonFile();
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
