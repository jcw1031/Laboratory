package com.woopaca.laboratory.thread.concurrency;

import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.Duration;

@EnableAsync
@Configuration
public class AsynchronousSpringEventConfiguration {

    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        TaskExecutor taskExecutor = new ThreadPoolTaskExecutorBuilder()
                .corePoolSize(10)
                .maxPoolSize(50)
                .queueCapacity(200)
                .threadNamePrefix("EventHandler-")
                .keepAlive(Duration.ofSeconds(60))
                .allowCoreThreadTimeOut(true)
                .build();
        eventMulticaster.setTaskExecutor(taskExecutor);
        return eventMulticaster;
    }
}
