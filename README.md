# spring-batch-study
> `jojoldu`님의 블로그를 기반으로 작성된 repository 입니다.

### Terms of glossary
- **Job**: 하나의 배치 작업 단위로 Job 안에는 여러개의 Step이 존재할 수 있다.
- **Step**
  - 실제 작업을 수행하는 역할로 Batch로 실제 처리하고자 하는 기능과 설정을 모두 포함한다.
  - `Step` 안에는 `Tasklet` 혹은 `Reader & Processor & Writer` 묶음이 존재하는데 하나의 `Step` 안에서 두개가 동시에 사용될 순 없다.
- Meta tables
  - BATCH_JOB_INSTANCE
    - `Job`이 실행될 때 생성되는 `JobInstance`에 관한 정보를 저장하고 있다.
    - `Job`이 실행될 때마다 해당 테이블에 기록되진 않고 `Job Parameter`(외부에서 전달 받는 파라미터)에 따라서 해당 테이블에 데이터가 저장되는데 이미 동일한 파라미터로 실행된 `Job`이 성공한 경우에는 테이블에 데이터가 저장되진 않는다.
  - BATCH_JOB_EXECUTION
    - `Job` 실행에 대한 성공/실패 여부등 `Job` 실행에 대한 내역을 가지고 있다.
  - BATCH_JOB_EXECUTION_PARAMS
    - `BATCH_JOB_EXECUTION` 테이블이 생성될 당시에 입력 받은 `Job Parameter` 정보를 가지고 있다. 

### Index
- [1. Spring Batch 프로젝트 생성하기](https://jojoldu.tistory.com/325?category=902551)
- [2. 메타테이블 엿보기](https://jojoldu.tistory.com/326?category=902551)
