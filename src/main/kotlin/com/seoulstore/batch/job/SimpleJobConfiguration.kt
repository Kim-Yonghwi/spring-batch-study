package com.seoulstore.batch.job

import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SimpleJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun simpleJob(): Job {
        // simpleJob 이란 이름의 Job 생성
        return jobBuilderFactory.get("simpleJob")
            .start(simpleStep1(null))
            .next(simpleStep2(null))
            .build()
    }

    @Bean
    @JobScope
    fun simpleStep1(
        @Value("#{jobParameters[requestDate]}") requestDate: String?
    ): Step {
        // simpleStep1 이란 이름의 Step 생성
        return stepBuilderFactory["simpleStep1"]
            // tasklet: step 안에서 수행될 기능을 명시
            .tasklet { contribution: StepContribution?, chunkContext: ChunkContext? ->
                log.info(">>>>> This is Step1");
                log.info(">>>>> requestDate = {}", requestDate);
                RepeatStatus.FINISHED
            }
            .build()
    }

    @Bean
    @JobScope
    fun simpleStep2(
        @Value("#{jobParameters[requestDate]}") requestDate: String?
    ): Step {
        // simpleStep1 이란 이름의 Step 생성
        return stepBuilderFactory["simpleStep2"]
            // tasklet: step 안에서 수행될 기능을 명시
            .tasklet { contribution: StepContribution?, chunkContext: ChunkContext? ->
                log.info(">>>>> This is Step2");
                log.info(">>>>> requestDate = {}", requestDate);
                RepeatStatus.FINISHED
            }
            .build()
    }
}
