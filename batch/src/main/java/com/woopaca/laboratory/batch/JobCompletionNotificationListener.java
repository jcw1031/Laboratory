package com.woopaca.laboratory.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private final JdbcClient jdbcClient;

    public JobCompletionNotificationListener(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB COMPLETED! Time to verify the results");

            jdbcClient.sql("SELECT first_name, last_name FROM people")
                    .query(Person.class)
                    .stream()
                    .forEach(person -> log.info("FOUND <{{}}> in the database", person));
        }
    }
}
