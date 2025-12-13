package com.woopaca.laboratory.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.partition.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.database.JdbcPagingItemReader;
import org.springframework.batch.infrastructure.item.database.Order;
import org.springframework.batch.infrastructure.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
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
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Configuration
public class PartitionBatchConfiguration {

    private final AtomicInteger atomicInteger = new AtomicInteger(0);
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
                .next(new StepBuilder("atomicInteger", jobRepository)
                        .tasklet((contribution, chunkContext) -> {
                            log.info("atomicInteger: {}", atomicInteger.get());
                            return RepeatStatus.FINISHED;
                        }, transactionManager)
                        .build())
                .build();
    }

    @Bean
    public Step partitionStep1(Partitioner dateRangePartitioner, Step slaveStep, TaskExecutor partitionTaskExecutor) {
        return new StepBuilder("partitionStep1", jobRepository)
                .partitioner("slaveStep", dateRangePartitioner)
                .step(slaveStep)
                .gridSize(5)
                .taskExecutor(partitionTaskExecutor)
                .build();
    }

    @Bean
    public Step slaveStep(@Qualifier("partitionReader") ItemReader<String> partitionReader, ItemProcessor<String, String> partitionProcessor) {
        return new StepBuilder("slaveStep", jobRepository)
                .<String, String>chunk(1000)
                .transactionManager(transactionManager)
                .reader(partitionReader)
                .processor(partitionProcessor)
                .writer(chunk -> atomicInteger.incrementAndGet())
                .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<String> partitionReader(
            @Value("#{stepExecutionContext['startDate']}") LocalDate startDate,
            @Value("#{stepExecutionContext['endDate']}") LocalDate endDate
    ) throws Exception {
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
                .pageSize(1000)
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

            LocalDate startDate = LocalDate.of(1986, 1, 1);
            LocalDate endDate = LocalDate.of(1987, 1, 1);

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

            log.info("Total partitions created: {}", partitions.size());
            return partitions;
        };
    }

    @Bean
    public StepExecutionListener partitionStepListener() {
        return new StepExecutionListener() {

            @Override
            public void beforeStep(StepExecution stepExecution) {
                log.info("▶️ Starting Step: {}", stepExecution.getStepName());
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                log.info("✅ Finished Step: {}", stepExecution.getStepName());
                return ExitStatus.COMPLETED;
            }
        };
    }

    @Bean
    public TaskExecutor partitionTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("partition-");
        return executor;
    }
}
