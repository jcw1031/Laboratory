package com.woopaca.laboratory.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    @Value("sample-data.csv")
    private Resource csvFile;

    private final JdbcTemplate jdbcTemplate;

    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB COMPLETED! Time to verify the results");

            jdbcTemplate.query("SELECT first_name, last_name FROM people", new DataClassRowMapper<>(Person.class))
                    .forEach(person -> log.info("FOUND <{{}}> in the database", person));
        }
    }
}
