package com.example.Attendance.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class JobScheduler {

    private final JobLauncher jobLauncher;
    private final Job attendanceJob;

//    @Scheduled(cron = "0 * * * * *")  // 매일 새벽 4시 실행
@Scheduled(cron = "0 0 4 * * *")
//    @Scheduled(cron = "0 */5 * * * *")
    public void runJob() {
        String dateParam = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("date", dateParam)  // 날짜 파라미터 추가
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            log.info("급여 이체 Job 시작: {}", dateParam);

            JobExecution execution = jobLauncher.run(attendanceJob, jobParameters);

            log.info("Job 실행 완료 - Status: {}, Start: {}, End: {}",
                    execution.getStatus(),
                    execution.getStartTime(),
                    execution.getEndTime());

            if (execution.getStatus() == BatchStatus.FAILED) {
                log.error("Job 실행 실패 - Exit Description: {}",
                        execution.getExitStatus().getExitDescription());
            }

        } catch (JobExecutionAlreadyRunningException e) {
            log.error("Job이 이미 실행 중입니다", e);
        } catch (JobRestartException e) {
            log.error("Job을 재시작할 수 없습니다", e);
        } catch (JobInstanceAlreadyCompleteException e) {
            log.error("이미 완료된 Job입니다", e);
        } catch (JobParametersInvalidException e) {
            log.error("잘못된 Job 파라미터입니다", e);
        } catch (Exception e) {
            log.error("Job 실행 중 예외 발생: {}", e.getMessage(), e);
        }
    }
}