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
class StepNextConditionalJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory
) {
    private val log = LoggerFactory.getLogger(StepNextConditionalJobConfiguration::class.java)

    /*
        on()
            - 캐치할 ExitStatus 지정
            - * 일 경우 모든 `ExitStatus`가 지정된다. (BatchStatus 아님)
        to()
            - 다음으로 이동할 Step 지정
        from()
            - 일종의 이벤트 리스너 역할
            - 상태값을 보고 일치하는 상태라면 to()에 포함된 step을 호출합니다.
            - step1의 이벤트 캐치가 FAILED로 되있는 상태에서 추가로 이벤트 캐치하려면 from을 써야만 함
        end()
            - `end`는 `FlowBuilder`를 반환하는 `end`와 `FlowBuilder`를 종료하는 end 2개가 있음
            - `on("*")`뒤에 있는 `end`는 `FlowBuilder`를 반환하는 `end`
            - `build()` 앞에 있는 `end`는 `FlowBuilder`를 종료하는 `end`
            - `FlowBuilder`를 반환하는 end 사용시 계속해서 `from`을 이어갈 수 있음
     */
    @Bean
    fun stepNextConditionalJob(): Job {
        return jobBuilderFactory["stepNextConditionalJob"]
            .start(conditionalJobStep1())
                .on("FAILED") // FAILED 일 경우
                .to(conditionalJobStep3()) // step3으로 이동한다.
                .on("*") // step3의 결과 관계 없이
                .end() // step3으로 이동하면 Flow가 종료한다.
            .from(conditionalJobStep1()) // step1로부터
                .on("*") // FAILED 외에 모든 경우
                .to(conditionalJobStep2()) // step2로 이동한다.
                .next(conditionalJobStep3()) // step2가 정상 종료되면 step3으로 이동한다.
                .on("*") // step3의 결과 관계 없이
                .end() // step3으로 이동하면 Flow가 종료한다. `FlowBuilder`를 반환
            .end() // Job 종료, `FlowBuilder`를 종료
            .build()
    }

    @Bean
    fun conditionalJobStep1(): Step {
        return stepBuilderFactory["step1"]
            .tasklet { contribution: StepContribution, chunkContext: ChunkContext? ->
                log.info(">>>>> This is stepNextConditionalJob Step1")
                /**
                 * ExitStatus를 FAILED로 지정한다. (step1 -> step3)
                 * 해당 status를 보고 flow가 진행된다.
                 */
                // contribution.exitStatus = ExitStatus.FAILED
                RepeatStatus.FINISHED
            }
            .build()
    }

    @Bean
    fun conditionalJobStep2(): Step {
        return stepBuilderFactory["conditionalJobStep2"]
            .tasklet { contribution: StepContribution?, chunkContext: ChunkContext? ->
                log.info(">>>>> This is stepNextConditionalJob Step2")
                RepeatStatus.FINISHED
            }
            .build()
    }

    @Bean
    fun conditionalJobStep3(): Step {
        return stepBuilderFactory["conditionalJobStep3"]
            .tasklet { contribution: StepContribution?, chunkContext: ChunkContext? ->
                log.info(">>>>> This is stepNextConditionalJob Step3")
                RepeatStatus.FINISHED
            }
            .build()
    }
}
