package com.hytsnbr.shiny_colors.step.tasklet;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.hytsnbr.shiny_colors.config.ApplicationConfig;
import com.hytsnbr.shiny_colors.constant.JsonFileName;
import com.hytsnbr.shiny_colors.dto.CdInfo;
import com.hytsnbr.shiny_colors.dto.CdInfoListJson;
import com.hytsnbr.shiny_colors.service.FileOperator;

/** JSONデータ作成タスクレット */
@Component
public class GenerateDataJsonTasklet implements Tasklet {

    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(GenerateDataJsonTasklet.class);

    private final ApplicationConfig appConfig;
    private final FileOperator fileOperator;

    /** コンストラクタ */
    public GenerateDataJsonTasklet(ApplicationConfig appConfig, FileOperator fileOperator) {
        this.appConfig = appConfig;
        this.fileOperator = fileOperator;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        // 前回作成したファイルの作成日チェック
        if (isCreationDateToday()) {
            logger.info("前回のファイル作成処理から日付が変わっていないため処理は中止されました");

            return RepeatStatus.FINISHED;
        }

        var cdInfoList =
                List.of(fileOperator.readJsonFile(JsonFileName.CD_INFO_LIST_JSON, CdInfo[].class));

        // 前回処理後のデータと一致する場合はファイル出力しない
        if (!matchPrevCdInfoList(cdInfoList)) {
            fileOperator.outputToJsonFile(CdInfoListJson.of(cdInfoList), JsonFileName.DATA_JSON);

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
        if (appConfig.isForce()) return false;

        var data = fileOperator.readJsonFile(JsonFileName.DATA_JSON, CdInfoListJson.class);
        if (Objects.isNull(data)) {
            return false;
        }

        final var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        final var createdAt =
                LocalDateTime.ofInstant(
                                Instant.ofEpochSecond(data.getCreatedAt()), ZoneId.systemDefault())
                        .format(formatter);
        final var today = LocalDateTime.now().format(formatter);

        logger.info("ファイル作成日: {}", createdAt);
        logger.info("処理日: {}", today);

        return Strings.CS.equals(createdAt, today);
    }

    /**
     * 前回処理時のデータと一致するか
     *
     * @param cdInfoList CD情報リスト
     * @return 一致する場合は true
     */
    private boolean matchPrevCdInfoList(List<CdInfo> cdInfoList) {
        if (appConfig.isForce()) return false;

        var data = fileOperator.readJsonFile(JsonFileName.DATA_JSON, CdInfoListJson.class);
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
