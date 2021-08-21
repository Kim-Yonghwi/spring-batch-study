package com.seoulstore.batch.job

import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.job.flow.FlowExecutionStatus
import org.springframework.batch.core.job.flow.JobExecutionDecider
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

/*
`StepNextConditionalJobConfiguration`은 다음 2가지 문제가 있다.
  - `Step`이 담당하는 역할이 2개 이상이 된다.
    - 실제 해당 `Step`이 처리해야할 로직외에도 분기처리를 시키기 위해 `ExitStatus` 조작이 필요합니다.
  - 다양한 분기 로직 처리의 어려움
    - `ExitStatus`를 커스텀하게 고치기 위해선 `Listener`를 생성하고 `Job Flow`에 등록하는 등 번거로움이 존재합니다.
 */

@Configuration
class DecideJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory
) {
    private val log = LoggerFactory.getLogger(DecideJobConfiguration::class.java)

    @Bean
    fun deciderJob(): Job {
        return jobBuilderFactory["deciderJob"]
            .start(startStep())
            .next(decider()) // 홀수 | 짝수 구분
            .from(decider()) // `decider`의 상태가
                .on("ODD") // `ODD`라면
                .to(oddStep()) // `oddStep`로 간다.
            .from(decider()) // `decider`의 상태가
                .on("EVEN") // `EVEN`이라면
                .to(evenStep()) // `evenStep`로 간다.
            .end() // builder 종료
            .build()
    }

    @Bean
    fun startStep(): Step {
        return stepBuilderFactory["startStep"]
            .tasklet { contribution: StepContribution?, chunkContext: ChunkContext? ->
                log.info(">>>>> Start!")
                RepeatStatus.FINISHED
            }
            .build()
    }

    @Bean
    fun evenStep(): Step {
        return stepBuilderFactory["evenStep"]
            .tasklet { contribution: StepContribution?, chunkContext: ChunkContext? ->
                log.info(">>>>> 짝수입니다.")
                RepeatStatus.FINISHED
            }
            .build()
    }

    @Bean
    fun oddStep(): Step {
        return stepBuilderFactory["oddStep"]
            .tasklet { contribution: StepContribution?, chunkContext: ChunkContext? ->
                log.info(">>>>> 홀수입니다.")
                RepeatStatus.FINISHED
            }
            .build()
    }

    @Bean
    fun decider(): JobExecutionDecider {
        return OddDecider()
    }

    /*
        `Step`과는 명확히 역할과 책임이 분리
        `Decider`는 분기로직을 담당하고 `Step`은 `Job`을 처리하기 위한 로직을 실행한다.
     */
    class OddDecider : JobExecutionDecider {
        private val log = LoggerFactory.getLogger(OddDecider::class.java)

        override fun decide(jobExecution: JobExecution, stepExecution: StepExecution?): FlowExecutionStatus {
            val rand = Random()
            val randomNumber = rand.nextInt(50) + 1

            log.info("랜덤숫자: {}", randomNumber)

            return if (randomNumber % 2 == 0) {
                FlowExecutionStatus("EVEN")
            } else {
                FlowExecutionStatus("ODD")
            }
        }
    }
}
