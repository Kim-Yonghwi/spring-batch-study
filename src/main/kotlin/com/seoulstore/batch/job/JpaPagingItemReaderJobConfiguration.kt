package com.seoulstore.batch.job

import com.seoulstore.batch.entity.Product
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory


@Configuration
class JpaPagingItemReaderJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val entityManagerFactory: EntityManagerFactory
) {
    private val log = LoggerFactory.getLogger(JpaPagingItemReaderJobConfiguration::class.java)
    private val chunkSize = 5

    @Bean
    fun jpaPagingItemReaderJob(): Job {
        return jobBuilderFactory["jpaPagingItemReaderJob"]
            .start(jpaPagingItemReaderStep())
            .build()
    }

    @Bean
    fun jpaPagingItemReaderStep(): Step {
        return stepBuilderFactory["jpaPagingItemReaderStep"]
            .chunk<Product, Product>(chunkSize)
            .reader(jpaPagingItemReader())
            .writer(jpaPagingItemWriter())
            .build()
    }

    @Bean
    fun jpaPagingItemReader(): JpaPagingItemReader<Product> {
        return JpaPagingItemReaderBuilder<Product>()
            .name("jpaPagingItemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(chunkSize)
            .queryString("SELECT p FROM Product p WHERE id > 0") // 쿼리 로그를 확인해보니 page size 만큼 limit offset이 자동으로 추가된다.
            .build()
    }

    fun jpaPagingItemWriter(): ItemWriter<Product> {
        return ItemWriter<Product> { list ->
            for (product in list) {
                log.info("Current Product={}", product)
            }
        }
    }
}
