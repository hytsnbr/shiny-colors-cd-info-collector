package com.hytsnbr.shiny_colors.step.tasklet;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.hytsnbr.shiny_colors.config.ApplicationConfig;

/** 前処理タスクレット */
@Component
public class PreProcessTasklet implements Tasklet {

    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(PreProcessTasklet.class);

    private final ApplicationConfig appConfig;

    /** コンストラクタ */
    public PreProcessTasklet(ApplicationConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        // コンテキスト取得
        var executeContext =
                chunkContext
                        .getStepContext()
                        .getStepExecution()
                        .getJobExecution()
                        .getExecutionContext();

        // バッチ処理内パラメータ名格納オブジェクト生成
        var batchJobParamNames = appConfig.getBatchJobParamNames();

        // システム日時・日付を格納
        var localDateTime = LocalDateTime.now();
        executeContext.put(batchJobParamNames.getExecuteDate(), localDateTime.toLocalDate());
        executeContext.put(batchJobParamNames.getExecuteDatetime(), localDateTime);

        // 強制更新フラグの値を出力
        logger.info("強制更新：{}", appConfig.isForce());

        return RepeatStatus.FINISHED;
    }
}
