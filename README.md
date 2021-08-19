# spring-batch-study
> `jojoldu`님의 블로그를 기반으로 작성된 repository 입니다.

### Terms of glossary
- **Job**: 하나의 배치 작업 단위로 Job 안에는 여러개의 Step이 존재할 수 있다.
- **Step**
  - 실제 작업을 수행하는 역할로 Batch로 실제 처리하고자 하는 기능과 설정을 모두 포함한다.
  - `Step` 안에는 `Tasklet` 혹은 `Reader & Processor & Writer` 묶음이 존재하는데 하나의 `Step` 안에서 두개가 동시에 사용될 순 없다.

### Index
- [Spring Batch 프로젝트 생성하기](https://jojoldu.tistory.com/325?category=902551)
