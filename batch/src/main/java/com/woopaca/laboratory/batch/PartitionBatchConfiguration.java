package com.woopaca.laboratory.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class PartitionBatchConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    public PartitionBatchConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSource dataSource) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
    }

    @Bean
    public Job partitionJob(Step partitionStep1) {
        return new JobBuilder("partitionJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(partitionStep1)
                .build();
    }

    @Bean
    public Step partitionStep1(Partitioner dateRangePartitioner, Step slaveStep, TaskExecutor partitionTaskExecutor) {
        return new StepBuilder("partitionStep1", jobRepository)
                .partitioner("slaveStep", dateRangePartitioner)
                .step(slaveStep)
                .gridSize(10)
                .taskExecutor(partitionTaskExecutor)
                .build();
    }

    @Bean
    public Step slaveStep(@Qualifier("partitionReader") ItemReader<String> partitionReader, ItemProcessor<String, String> partitionProcessor) {
        return new StepBuilder("slaveStep", jobRepository)
                .<String, String>chunk(100, transactionManager)
                .reader(partitionReader)
                .processor(partitionProcessor)
                .writer(chunk -> log.info("chunk: {}", chunk))
                .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<String> partitionReader(
            @Value("#{stepExecutionContext['startDate']}") LocalDate startDate,
            @Value("#{stepExecutionContext['endDate']}") LocalDate endDate
    ) {
        log.info("partitionReader: startDate={}, endDate={}", startDate, endDate);

        return new JdbcPagingItemReaderBuilder<String>()
                .name("partitionReader")
                .dataSource(dataSource)
                .selectClause("s.emp_no, SUM(salary) AS total_salary")
                .fromClause("employees e JOIN salaries s ON s.emp_no = e.emp_no")
                .whereClause("s.from_date BETWEEN :startDate AND :endDate")
                .groupClause("e.emp_no")
                .sortKeys(Map.of("emp_no", Order.ASCENDING))
                .parameterValues(Map.of("startDate", startDate, "endDate", endDate))
                .rowMapper((rs, rowNum) -> {
                    String empNo = rs.getString("emp_no");
                    String totalSalary = rs.getString("total_salary");
                    return String.join("-", empNo, totalSalary);
                })
                .pageSize(100)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<String, String> partitionProcessor() {
        return item -> item;
    }

    @Bean
    public Partitioner dateRangePartitioner() {
        return gridSize -> {
            log.info("gridSize: {}", gridSize);

            LocalDate startDate = LocalDate.of(1981, 1, 1);
            LocalDate endDate = LocalDate.of(2002, 1, 1);

            Map<String, ExecutionContext> partitions = new HashMap<>();
            LocalDate currentStart = startDate;
            int partitionNumber = 0;

            while (currentStart.isBefore(endDate)) {
                ExecutionContext executionContext = new ExecutionContext();
                LocalDate currentEnd = currentStart.plusMonths(1)
                        .minusDays(1);
                if (currentEnd.isAfter(endDate)) {
                    currentEnd = endDate;
                }

                executionContext.put("startDate", currentStart);
                executionContext.put("endDate", currentEnd.plusDays(1));
                executionContext.putString("partitionKey", "partition-" + partitionNumber);

                partitions.put("partition" + partitionNumber, executionContext);

                currentStart = currentEnd.plusDays(1);
                partitionNumber++;
            }

            return partitions;
        };
    }

    @Bean
    public TaskExecutor partitionTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("partition-");
        return executor;
    }
}
