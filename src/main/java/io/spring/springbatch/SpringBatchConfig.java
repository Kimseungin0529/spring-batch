package io.spring.springbatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
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


    /**
     * 이처럼 flow 내부에서 흐름 처리를 하면 해당 Job(FlowJob)은 COMPLETED 처리가 된다. 하지만 Job 자체(FlowJob)에서 on 절 처리를 하더라도 FAIL 처리된다.
     * 만약에 flow() 내부에서 처리하는게 아니라 job1() 에서 on 절 처리를 하면 step1 성공/실패에 따라 step2 or step3가 실행되지만 job1의 FlowJob 은 FAIL 처리된다.
     */

    @Bean
    public Job job1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("job1", jobRepository)
                .start(flow(jobRepository, transactionManager))
                .end()
                .build();
    }


    @Bean
    @JobScope
    public Flow flow(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow");
        flowBuilder.start(step1(jobRepository, transactionManager))
                .on("COMPLETED").to(step2(jobRepository, transactionManager))
                .from(step1(jobRepository, transactionManager))
                .on("FAILED").to(step3(jobRepository, transactionManager))
                .end();
        return flowBuilder.build();

    }

    @Bean
    @JobScope
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        throw new RuntimeException("강제 실패");
                        //log.info("Spring Batch : job1-> step1 Success");
                        //return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                //.allowStartIfComplete(true)
                .build();
    }

    @Bean
    @JobScope
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step2", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        log.info("Spring Batch : job1-> step2 Success");
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .build();
    }

    @Bean
    @JobScope
    public Step step3(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step3", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        log.info("Spring Batch : job1-> step3 Success");
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .build();
    }

}
