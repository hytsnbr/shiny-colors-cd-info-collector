package com.hytsnbr.shiny_colors.config;

import java.nio.file.Paths;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hytsnbr.shiny_colors.dto.CdInfo;
import com.hytsnbr.shiny_colors.dto.DiscographyData;
import com.hytsnbr.shiny_colors.exception.CdInfoWebScrapingException;
import com.hytsnbr.shiny_colors.step.chunk.CdInfoDataProcessor;

/** バッチ設定 */
@Component
public class BatchConfig {

    private final ApplicationConfig appConfig;

    /** コンストラクタ */
    public BatchConfig(ApplicationConfig appConfig) {
        this.appConfig = appConfig;
    }

    /** メインジョブ設定 */
    @Bean
    public Job mainJob(
            JobRepository jobRepository,
            Step createDiscographyListStep,
            Step createCdInfoListStep,
            Step generateDataJsonStep,
            Step cleanupStep,
            JobExecutionListener jobExecutionListener) {
        return new JobBuilder("main_job", jobRepository)
                .start(createDiscographyListStep)
                .next(createCdInfoListStep)
                .next(generateDataJsonStep)
                .next(cleanupStep)
                .listener(jobExecutionListener)
                .build();
    }

    /** ディスコグラフィー情報作成ステップ設定 */
    @Bean
    public Step createDiscographyListStep(
            JobRepository jobRepository,
            Tasklet createDiscographyListTasklet,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("create_discography_list_step", jobRepository)
                .tasklet(createDiscographyListTasklet, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    /** CD情報リスト作成ステップ設定 */
    @Bean
    public Step createCdInfoListStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            JsonItemReader<DiscographyData> discographyDataListJsonReader,
            CdInfoDataProcessor cdInfoDataProcessor,
            JsonFileItemWriter<CdInfo> cdInfoJsonWriter) {
        return new StepBuilder("generate_data_json_step", jobRepository)
                .<DiscographyData, CdInfo>chunk(1, transactionManager)
                .reader(discographyDataListJsonReader)
                .processor(cdInfoDataProcessor)
                .writer(cdInfoJsonWriter)
                .allowStartIfComplete(true)
                .faultTolerant()
                .retryLimit(this.appConfig.getProcess().getRetryLimit())
                .retry(CdInfoWebScrapingException.class)
                .build();
    }

    /** JSONデータ作成ステップ設定 */
    @Bean
    public Step generateDataJsonStep(
            JobRepository jobRepository,
            Tasklet generateDataJsonTasklet,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("generate_data_json_step", jobRepository)
                .tasklet(generateDataJsonTasklet, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    /** クリーンアップ処理ステップ設定 */
    @Bean
    public Step cleanupStep(
            JobRepository jobRepository,
            Tasklet cleanupTasklet,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("cleanup_step", jobRepository)
                .tasklet(cleanupTasklet, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    /** ディスコグラフィーデータJSONファイル読み込み処理設定 */
    @Bean
    public JsonItemReader<DiscographyData> discographyDataListJsonReader() {
        final var jsonPath = Paths.get(this.appConfig.getJsonDirPath(), "DiscographyList.json");

        return new JsonItemReaderBuilder<DiscographyData>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(DiscographyData.class))
                .resource(new FileSystemResource(jsonPath))
                .name("discographyDataListJsonReader")
                .build();
    }

    /** CD情報JSON書き込み設定 */
    @Bean
    public JsonFileItemWriter<CdInfo> cdInfoJsonWriter() {
        final var jsonPath = Paths.get(this.appConfig.getJsonDirPath(), "CDInfoList.json");

        final var objectMapper = new ObjectMapper();
        // Jacksonで Java8 の LocalDate 関係を処理できるようにする
        objectMapper.registerModule(new JavaTimeModule());

        return new JsonFileItemWriterBuilder<CdInfo>()
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>(objectMapper))
                .resource(new FileSystemResource(jsonPath))
                .name("tradeJsonFileItemWriter")
                .build();
    }
}
