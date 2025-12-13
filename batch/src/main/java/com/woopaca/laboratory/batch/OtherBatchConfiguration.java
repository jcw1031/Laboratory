package com.woopaca.laboratory.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class OtherBatchConfiguration {

    @Bean
    public Job otherJob(JobRepository jobRepository, Step otherStep) {
        return new JobBuilder("other-job", jobRepository)
                .start(otherStep)
                .build();
    }

    @Bean
    public Step otherStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("other-step", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("다른 배치 잡");
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }
}
