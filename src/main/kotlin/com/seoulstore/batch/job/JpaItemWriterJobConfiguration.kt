package com.seoulstore.batch.job

import com.seoulstore.batch.entity.Product
import com.seoulstore.batch.entity.Product2
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory


@Configuration
class JpaItemWriterJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val entityManagerFactory: EntityManagerFactory
) {
    private val chunkSize = 5

    @Bean
    fun jpaItemWriterJob(): Job {
        return jobBuilderFactory.get("jpaItemWriterJob")
            .start(jpaItemWriterStep())
            .build()
    }

    @Bean
    fun jpaItemWriterStep(): Step {
        return stepBuilderFactory["jpaItemWriterStep"]
            .chunk<Product, Product2>(chunkSize)
            .reader(jpaItemWriterReader())
            .processor(jpaItemProcessor())
            .writer(jpaItemWriter())
            .build()
    }

    @Bean
    fun jpaItemWriterReader(): JpaPagingItemReader<Product> {
        return JpaPagingItemReaderBuilder<Product>()
            .name("jpaItemWriterReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(chunkSize)
            .queryString("SELECT p FROM Product p")
            .build()
    }

    @Bean
    fun jpaItemProcessor(): ItemProcessor<Product, Product2> {
        return ItemProcessor<Product, Product2> { p -> Product2(name = p.name) }
    }

    @Bean
    fun jpaItemWriter(): JpaItemWriter<Product2> {
        val jpaItemWriter: JpaItemWriter<Product2> = JpaItemWriter<Product2>()
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory)
        return jpaItemWriter
    }
}
