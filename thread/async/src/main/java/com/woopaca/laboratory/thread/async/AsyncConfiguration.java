package com.woopaca.laboratory.thread.async;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
public class AsyncConfiguration {

    @Bean
    public AsyncTask asyncTask(TaskExecutor taskExecutor) {
        return new AsyncTask();
    }
}
