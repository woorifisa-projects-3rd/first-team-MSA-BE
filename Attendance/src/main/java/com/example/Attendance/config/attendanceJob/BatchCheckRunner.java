package com.example.Attendance.config.attendanceJob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchCheckRunner {

    private final JobLauncher jobLauncher;
    private final Job attendanceJob;
    private final JobRepository jobRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void checkAndRunBatch() {

        LocalTime now = LocalTime.now();
        LocalTime targetTime = LocalTime.of(4, 0);
        if (now.isBefore(targetTime)) {
            log.info("4시 이전이므로 배치 체크 스킵");
            return;
        }

        String dateParam = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("date", dateParam)
                    .toJobParameters();

            JobExecution lastExecution = jobRepository
                    .getLastJobExecution("automaticTransferJob", params);

            if (lastExecution == null) {
                log.info("오늘 배치 미실행. 배치 실행");
                jobLauncher.run(attendanceJob, params);
            }
        } catch (Exception e) {
            log.error("서버 시작시 배치 체크 실패: {}", e.getMessage());
        }
    }
}