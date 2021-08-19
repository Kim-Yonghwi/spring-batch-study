package com.seoulstore.batch.job

import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SimpleJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory
) {
    private val log = LoggerFactory.getLogger(SimpleJobConfiguration::class.java)

    @Bean
    fun simpleJob(): Job {
        // simpleJob 이란 이름의 Job 생성
        return jobBuilderFactory.get("simpleJob")
            .start(simpleStep1())
            .build()
    }

    @Bean
    fun simpleStep1(): Step {
        // simpleStep1 이란 이름의 Step 생성
        return stepBuilderFactory["simpleStep1"]
            // tasklet: step 안에서 수행될 기능을 명시
            .tasklet { contribution: StepContribution?, chunkContext: ChunkContext? ->
                log.info(">>>>> This is Step1")
                RepeatStatus.FINISHED
            }
            .build()
    }
}
