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
  - `Spring Batch`에서는 `Step`들의 `Flow`속에서 분기만 담당하는 역할을 한다.

### Index
- [1. Spring Batch 프로젝트 생성하기](https://jojoldu.tistory.com/325?category=902551)
- [2. 메타테이블 엿보기](https://jojoldu.tistory.com/326?category=902551)
- [3. Spring Batch Job Flow](https://jojoldu.tistory.com/328?category=902551)
