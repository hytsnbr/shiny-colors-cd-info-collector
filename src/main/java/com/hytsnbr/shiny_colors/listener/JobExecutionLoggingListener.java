package com.hytsnbr.shiny_colors.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.stereotype.Component;

@JobScope
@Component
public class JobExecutionLoggingListener implements JobExecutionListener {
    
    /** ロガー */
    private static final Logger logger = LoggerFactory.getLogger(JobExecutionLoggingListener.class);
    
    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Before Job");
    }
    
    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.info("After Job");
    }
}
