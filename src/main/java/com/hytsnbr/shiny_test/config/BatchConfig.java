package com.hytsnbr.shiny_test.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * バッチ設定
 */
@Component
public class BatchConfig {
    
    /**
     * ジョブ設定
     */
    @Bean
    public Job mainJob(
        JobRepository jobRepository,
        Step mainStep,
        JobExecutionListener jobExecutionListener
    ) {
        return new JobBuilder("main_job", jobRepository)
            .start(mainStep)
            .listener(jobExecutionListener)
            .build();
    }
    
    /**
     * ジョブ内ステップ設定
     */
    @Bean
    public Step mainStep(
        JobRepository jobRepository,
        Tasklet mainTasklet,
        PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("main_step", jobRepository)
            .tasklet(mainTasklet, transactionManager)
            .allowStartIfComplete(true)
            .build();
    }
}
