package com.seoulstore.batch.job

import com.seoulstore.batch.entity.Product
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory


@Configuration
class ProcessorConvertJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val entityManagerFactory: EntityManagerFactory,
    @Value("\${chunk_size}")
    private val chunkSize: Int
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun processorConvertJob(): Job {
        return jobBuilderFactory["processorConvertJob"]
            .preventRestart()
            .start(processorConvertStep())
            .build()
    }

    @Bean
    fun processorConvertStep(): Step {
        return stepBuilderFactory["processorConvertStep"]
            .chunk<Product, String>(chunkSize)
            .reader(processorConvertReader())
            .processor(processor())
            .writer(writer())
            .build()
    }

    @Bean
    fun processorConvertReader(): JpaPagingItemReader<Product> {
        return JpaPagingItemReaderBuilder<Product>()
            .name("processorConvertReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(chunkSize)
            .queryString("SELECT p FROM Product p")
            .build()
    }

    @Bean
    fun processor(): ItemProcessor<Product, String> {
        return ItemProcessor<Product, String> { p -> p.name }
    }

    @Bean
    fun writer(): ItemWriter<String> {
        return ItemWriter<String> { items ->
            log.info("Product Names={}", items)
        }
    }
}
