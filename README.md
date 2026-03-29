### 캘린더 기반 할 일 관리 서비스
Spring Boot을 활용하여 캘린더 기반의 할 일을 관리하는 웹 서비스의 백엔드 서버입니다. JWT 기반의 사용자 인증과 할 일 관리, 캘린더 공유, 유저 검색, 명언 제공 기능을 RESTful API로 제공합니다.

### 진행 기간
2026.03.18. ~ 2026.04.01.

### 프로젝트 목표
- Spring Boot Controller → Service → Repository 3계층 구조 설계 및 구현
- JWT 기반의 인증 시스템 구현 및 Security Filter Chain 구조 이해
- JPA를 활용한 데이터 접근과 JPQL 커스텀 쿼리 작성
- API 명세서를 기반으로 프론트엔드와의 사전 합의 및 기능 설계
- Docker 기반 클라우드 배포 환경 구성
- 공통 응답 구조와 전역 예외 처리를 통한 일관된 통신 패턴 구축

### 사용 기술
- Java
- Spring Boot
- JPA
- PostgreSQL
- JWT
- JUnit
- AWS EC2, Docker

### 파일 구조
```
goorm-todo-back/
└── src/main/java/
    ├── controller/       # HTTP 요청 처리 (API 엔드포인트)
    ├── service/          # 비즈니스 로직
    ├── repository/       # 데이터 접근 (JPA)
    ├── domain/           # Entity 클래스
    ├── dto/              # 요청/응답 DTO 클래스
    ├── jwt/         # JWT 필터 및 인증 처리
```

### ERD
<img width="667" height="379" alt="스크린샷 2026-03-26 오후 3 36 10" src="https://github.com/user-attachments/assets/c8de06ee-27f3-48ae-a33d-72617b5d0368" />

### 주요 기능

**인증/유저**
- 회원가입 및 로그인
- 닉네임 기반 유저 검색

**할 일**
- 할 일 생성
- 할 일 단건 및 목록 조회
- 할 일 수정 및 완료 여부 체크
- 할 일 삭제

**친구**
- 친구 요청 전송 및 수락/거절
- 받은 친구 요청 목록 및 친구 목록 조회
- 친구 캘린더 열람
- 친구 삭제

**명언**
- 명언 등록
- 랜덤 명언 조회

### API 명세서
- `POST /api/v1/auth/register` 회원가입
- `POST /api/v1/auth/login` 로그인
- `GET /api/v1/users/search` 유저 검색
- `GET /api/v1/todos` 할 일 목록 조회
- `GET /api/v1/todos/{todoId}` 할 일 단건 조회
- `POST /api/v1/todos` 할 일 생성
- `PATCH /api/v1/todos/{todoId}` 할 일 수정 및 완료 체크
- `DELETE /api/v1/todos/{todoId}` 할 일 삭제
- `POST /api/v1/friends/request` 친구 요청 전송
- `PATCH /api/v1/friends/request/{friendId}` 친구 요청 수락/거절
- `GET /api/v1/friends/request/received` 받은 친구 요청 목록 조회
- `GET /api/v1/friends` 친구 목록 조회
- `GET /api/v1/friends/{friendId}/calendar` 친구 캘린더 조회
- `DELETE /api/v1/friends/{friendId}` 친구 삭제
- `POST /api/v1/quotes` 명언 등록
- `GET /api/v1/quotes/random` 랜덤 명언 조회





