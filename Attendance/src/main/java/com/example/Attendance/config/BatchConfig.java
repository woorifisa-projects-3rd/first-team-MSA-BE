package com.example.Attendance.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class BatchConfig {

    @Bean
    public Job autopayJob(JobRepository jobRepository, Step autopayStep) {
        return new JobBuilder("autopayJob", jobRepository)
                .start(autopayStep)
                .build();
    }

    @Bean
    public Step autopayStep(JobRepository jobRepository, Tasklet autopayTasklet, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("autopayStep", jobRepository)
                .tasklet(autopayTasklet, platformTransactionManager).build();
    }

    @Bean
    public Tasklet autopayTasklet(){
        return ((contribution, chunkContext) -> {
            log.info("autopayTasklet 실행되고 있어욤!!");
            return RepeatStatus.FINISHED;
        });
    }
}
