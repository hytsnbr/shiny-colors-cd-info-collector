package com.hytsnbr.shiny_colors.step.tasklet;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.hytsnbr.shiny_colors.config.ApplicationConfig;
import com.hytsnbr.shiny_colors.constant.JsonFileName;

/** クリーンアップ処理タスクレット */
@Component
public class CleanupTasklet implements Tasklet {

    private final ApplicationConfig appConfig;

    /** コンストラクタ */
    public CleanupTasklet(ApplicationConfig appConfig) {
        this.appConfig = appConfig;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
            throws Exception {
        // 処理中に作成したコミット対象外のファイルを削除する
        Files.deleteIfExists(Paths.get(appConfig.getJsonDirPath(), JsonFileName.CD_INFO_LIST_JSON));
        Files.deleteIfExists(
                Paths.get(appConfig.getJsonDirPath(), JsonFileName.DISCOGRAPHY_LIST_JSON));

        return RepeatStatus.FINISHED;
    }
}
