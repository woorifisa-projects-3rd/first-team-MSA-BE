package com.example.Attendance.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BatchScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job autopayJob;

    @Scheduled(cron = "30 * * * * *") // 매 분의 30초에 실행하겠다
    public void runJob() throws Exception{
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("currentTime", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(autopayJob, jobParameters);
        log.info("30초다!! autopayJob 실행됐어욤");
    }
}
