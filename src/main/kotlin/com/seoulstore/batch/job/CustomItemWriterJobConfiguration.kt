package com.seoulstore.batch.job

import com.seoulstore.batch.entity.Product
import com.seoulstore.batch.entity.Product2
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory


@Configuration
class CustomItemWriterJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val entityManagerFactory: EntityManagerFactory
) {
    private val log = LoggerFactory.getLogger(CustomItemWriterJobConfiguration::class.java)
    private val chunkSize = 5

    @Bean
    fun customItemWriterJob(): Job {
        return jobBuilderFactory["customItemWriterJob"]
            .start(customItemWriterStep())
            .build()
    }

    @Bean
    fun customItemWriterStep(): Step {
        return stepBuilderFactory["customItemWriterStep"]
            .chunk<Product, Product2>(chunkSize)
            .reader(customItemWriterReader())
            .processor(customItemWriterProcessor())
            .writer(customItemWriter())
            .build()
    }

    @Bean
    fun customItemWriterReader(): JpaPagingItemReader<Product> {
        return JpaPagingItemReaderBuilder<Product>()
            .name("customItemWriterReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(chunkSize)
            .queryString("SELECT p FROM Product p")
            .build()
    }

    @Bean
    fun customItemWriterProcessor(): ItemProcessor<Product, Product2> {
        return ItemProcessor<Product, Product2> { p -> Product2(name = p.name) }
    }

    @Bean
    fun customItemWriter(): ItemWriter<Product2> {
        return ItemWriter<Product2> { items ->
            for (item in items) {
                log.info("Product2 = {}", item)
            }
        }
    }
}
