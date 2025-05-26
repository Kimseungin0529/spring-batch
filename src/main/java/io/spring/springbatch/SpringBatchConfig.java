package io.spring.springbatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SpringBatchConfig {


    @Bean
    public Job job1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("job1", jobRepository)
                .start(step1(jobRepository, transactionManager)) // step 설정
                .next(step2(jobRepository, transactionManager))
                .build();
    }


    @Bean
    @JobScope
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(testTasklet1(), transactionManager)
                .build();
    }

    @Bean
    @JobScope
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step2", jobRepository)
                .tasklet(testTasklet2(), transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet testTasklet1() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                log.info("=================================");
                log.info("Spring Batch : job1-> step1 Success");
                log.info("=================================");
                return RepeatStatus.FINISHED; // 작업에 대한 Status 명시
            }
        };
    }

    @Bean
    @StepScope
    public Tasklet testTasklet2() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                log.info("=================================");
                log.info("Spring Batch : job1-> step2 Success");
                log.info("=================================");
                return RepeatStatus.FINISHED; // 작업에 대한 Status 명시
            }
        };
    }
}
