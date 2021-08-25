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
class ProcessorNullJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val entityManagerFactory: EntityManagerFactory,
    @Value("\${chunk_size}")
    private val chunkSize: Int
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun processorNullJob(): Job {
        return jobBuilderFactory["processorNullJob"]
            .preventRestart()
            .start(processorNullStep())
            .build()
    }

    @Bean
    fun processorNullStep(): Step {
        return stepBuilderFactory["processorNullStep"]
            .chunk<Product, Product>(chunkSize)
            .reader(processorNullReader())
            .processor(processorNullProcessor())
            .writer(processorNullWriter())
            .build()
    }

    @Bean
    fun processorNullReader(): JpaPagingItemReader<Product> {
        return JpaPagingItemReaderBuilder<Product>()
            .name("processorNullReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(chunkSize)
            .queryString("SELECT p FROM Product p")
            .build()
    }

    @Bean
    fun processorNullProcessor(): ItemProcessor<Product, Product> {
        return ItemProcessor<Product, Product> {
            val isIgnoreTarget = it.id!! % 2 == 0L

            if (isIgnoreTarget) {
                log.info(">>>>>>>>> Product name={}, isIgnoreTarget={}", it.name, isIgnoreTarget);
                // return null;을 함으로써 `Writer`에 넘기지 않도록 한다.
                return@ItemProcessor null
            }

            return@ItemProcessor it
        }
    }

    @Bean
    fun processorNullWriter(): ItemWriter<Product> {
        return ItemWriter<Product> { items ->
            log.info("Product Names={}", items)
        }
    }
}
