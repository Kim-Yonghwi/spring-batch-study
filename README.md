# spring-batch-study
> `jojoldu`님의 블로그를 기반으로 작성된 repository 입니다.

### Terms of glossary
- **Job**: 하나의 배치 작업 단위로 Job 안에는 여러개의 Step이 존재할 수 있다.
- **Step**
  - 실제 작업을 수행하는 역할로 Batch로 실제 처리하고자 하는 기능과 설정을 모두 포함한다.
  - `Step` 안에는 `Tasklet` 혹은 `Reader & Processor & Writer` 묶음이 존재하는데 하나의 `Step` 안에서 두개가 동시에 사용될 순 없다.
- **Meta tables**
  - BATCH_JOB_INSTANCE
    - `Job`이 실행될 때 생성되는 `JobInstance`에 관한 정보를 저장하고 있다.
    - `Job`이 실행될 때마다 해당 테이블에 기록되진 않고 `Job Parameter`(외부에서 전달 받는 파라미터)에 따라서 해당 테이블에 데이터가 저장되는데 이미 동일한 파라미터로 실행된 `Job`이 성공한 경우에는 테이블에 데이터가 저장되진 않는다.
  - BATCH_JOB_EXECUTION
    - `Job` 실행에 대한 성공/실패 여부등 `Job` 실행에 대한 내역을 가지고 있다.
  - BATCH_JOB_EXECUTION_PARAMS
    - `BATCH_JOB_EXECUTION` 테이블이 생성될 당시에 입력 받은 `Job Parameter` 정보를 가지고 있다.
- **Next**
  - 여러 `Step` 간에 순서 혹은 처리 흐름을 제어한다.
  - 순차적으로 `Step`을 연결시킬 때 사용한다.
    - step1 -> step2 -> step3
    - step1에서 에러가 발생할 경우 step2, step3은 실행되지 않지만 상황에 따라 step1이 성공했을 때는 step2를 실패했을 때는 step3을 실행하도록 할 수 있다.
- **BatchStatus**
  - `Job` 또는 `Step`의 실행 결과를 `BATCH_JOB_EXECUTION`의 `STATUS` 컬럼에 기록할 때 사용하는 `enum`이다.
  - `COMPLETED`, `STARTING`, `STARTED`, `STOPPING`, `STOPPED`, `FAILED`, `ABANDONED`, `UNKNOWN` 등이 있다.
- **ExitStatus**
  - Step의 실행 후 상태를 뜻한다.
  - 기본적으로 `ExitStatus`의 `exitCode`는 `Step`의 `BatchStatus`와 같도록 설정이 되어 있다.
- **JobExecutionDecider**
  - `Spring Batch`에서는 `Step`의 `Flow`속에서 분기만 담당하는 역할을 한다.
- **Job Parameter**
  - 외부 혹은 내부에서 파라미터를 받아 여러 Batch 컴포넌트에서 사용할 수 있다.
  - `Job Parameter`를 사용하기 위해서는 `Spring Batch` 전용 `Scope`를 꼭 선언해야 한다.
    - 크게 `@StepScope`와 `@JobScope` 2가지가 있다.
      - 위 2개의 `Scope`는 `Step` 또는 `Job`실행 시점에 해당 컴포넌트를 `Spring Bean`으로 생성한다.
      - `JobParameter`의 `Late Binding`이 가능하다. 즉, `Application`이 실행되는 시점이 아니더라도 `Controller`나 `Service`와 같은 비지니스 로직 처리 단계에서 `Job Parameter`를 할당시킬 수 있다.
      - 동일한 컴포넌트를 병렬 혹은 동시에 사용할때 유용하다.
    - 아래와 같이 `SpEL`로 선언해서 사용 가능하다.
    - ```
      @Value("#{jobParameters[파라미터명]}")
      ```
    - `jobParameters` 외에도 `jobExecutionContext`, `stepExecutionContext` 등도 `SpEL`로 사용할 수 있고 `@JobScope`에선 `stepExecutionContext`는 사용할 수 없다.
    - `@JobScope`는 `Step` 선언문에서 사용 가능하고, `@StepScope`는 `Tasklet`이나 `ItemReader, ItemWriter, ItemProcessor`에서 사용할 수 있다.
- **Chunk**
  - 데이터 덩어리로, chunk 지향 처리란 한 번에 하나씩 데이터를 읽어 `Chunk`라는 덩어리를 만든 뒤, `Chunk` 단위로 트랜잭션을 다루는 것을 의미한다.
  - `Chunk` 단위로 트랜잭션을 수행하기 때문에 실패할 경우엔 해당 `Chunk` 만큼만 롤백이 되고, 이전에 커밋된 트랜잭션 범위까지는 반영이 된다.
  - 처리 순서
    - `Reader`에서 데이터를 하나 읽어온다.
    - 읽어온 데이터를 `Processor`에서 가공한다.
    - 가공된 데이터들을 별도의 공간에 모은 뒤, `Chunk` 단위만큼 쌓이게 되면 `Writer`에 전달하고 `Writer`는 일괄 저장한다.
  - `Chunk Size`는 한번에 처리될 트랜잭션 단위를 얘기하며, `Page Size`는 한번에 조회할 `Item`의 `size`를 뜻한다.

### 학습 목차
- [1. Spring Batch 프로젝트 생성하기](https://jojoldu.tistory.com/325?category=902551)
- [2. 메타테이블 엿보기](https://jojoldu.tistory.com/326?category=902551)
- [3. Spring Batch Job Flow](https://jojoldu.tistory.com/328?category=902551)
- [4. Spring Batch Scope & Job Parameter](https://jojoldu.tistory.com/330?category=902551)
- [5. Chunk 지향 처리](https://jojoldu.tistory.com/331?category=902551)

### Document
- [Spring Batch 공식 문서](https://docs.spring.io/spring-batch/docs/4.0.x/reference/html/index-single.html#spring-batch-intro)
