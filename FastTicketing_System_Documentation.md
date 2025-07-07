# FastTicketing System Documentation

## 개요

FastTicketing은 대규모 동시 접속 상황에서 공연 티켓 예매를 안정적으로 처리할 수 있도록 설계된 고성능 좌석 기반 티켓 예매 시스템입니다. 마이크로서비스 아키텍처를 기반으로 하며, 분산 락, 큐 관리, Redis 캐싱을 통해 높은 트래픽 상황에서도 안정적인 예매 서비스를 제공합니다.

## 시스템 아키텍처

### 마이크로서비스 구조

#### Backend Services
- **`common`** - 공통 설정, 유틸리티, 예외 처리, 공통 관심사
- **`user-service`** - 사용자 관리 및 인증
- **`performance-service`** - 공연장, 홀, 공연 일정 및 좌석 관리
- **`ticket-service`** - 핵심 티켓팅 로직, 예약, 큐 관리, 예매 플로우
- **`app`** - 모든 서비스를 통합하는 메인 애플리케이션
- **`frontend`** - 관리자 및 사용자 인터페이스를 위한 React/TypeScript SPA

### 도메인 아키텍처 (Hexagonal Architecture)
각 서비스는 명확한 계층 분리를 통한 헥사고날 아키텍처를 따릅니다:
- `adapter/in` - 컨트롤러와 요청/응답 객체
- `adapter/out` - 리포지토리와 외부 연동
- `application` - 비즈니스 로직과 DTO를 포함한 서비스 계층
- `domain` - 엔티티, 열거형, 핵심 도메인 로직

## 기술 스택

### Backend
- **언어**: Java 17
- **프레임워크**: Spring Boot 3.4.2
- **데이터 액세스**: Spring Data JPA, QueryDSL
- **데이터베이스**: MySQL 8.0
- **캐싱/분산락**: Redis, Redisson
- **빌드 도구**: Gradle 8.12.1

### Frontend
- **언어**: TypeScript 5.8
- **프레임워크**: React 19
- **빌드 도구**: Vite 6.3
- **라우팅**: React Router DOM 7.6
- **패키지 관리**: npm

## 핵심 비즈니스 로직

### 티켓 예매 플로우

1. **큐 관리 시스템**
   - Redis SortedSet 기반 대기열 시스템
   - 높은 트래픽 상황에서 순차적 처리 보장

2. **좌석 예약 상태 머신**
   ```
   AVAILABLE → RESERVED → CANCEL_PENDING → AVAILABLE
   ```

3. **분산 락킹**
   - Redisson을 활용한 분산 락
   - 좌석 예매 시 레이스 컨디션 방지

4. **TTL 만료 처리**
   - 10분간의 결제 유예 시간
   - 스케줄러를 통한 자동 정리

### 동시성 처리 패턴

- **낙관적 락킹**: JPA 엔티티의 `@Version` 어노테이션 활용
- **분산 락**: Redisson을 사용한 임계 영역 보호
- **Redis 캐싱**: 좌석 가용성 캐시와 무효화 전략
- **스케줄드 태스크**: 예약 만료 및 정리 작업

### 데이터베이스 설계

#### 공연장 관리
```
Venue (공연장) → Hall (홀) → Seat (좌석)
```

#### 공연 관리
```
Performance (공연) → PerformanceSchedule (공연 일정)
```

#### 티켓 관리
- **TicketOpen**: 예매 오픈 시간 윈도우 제어
- **Reservation**: 예약 시스템 (상태 tracking 및 만료 처리)
- **Ticket**: 최종 티켓 정보

## 시스템 설정

### 서버 설정
- **포트**: 3573 (SSL 활성화, 커스텀 키스토어)
- **데이터베이스**: MySQL `springpay` 스키마
- **커넥션 풀**: HikariCP (5-20 연결)
- **Redis**: localhost:6379

### Frontend 설정
- **개발 서버**: Vite 개발 서버
- **API 프록시**: `/api` 경로를 `https://localhost:3573`으로 프록시

## 성능 최적화

시스템은 고트래픽 상황을 위해 최적화되었습니다:

- **대량 작업**: 좌석 생성을 위한 벌크 작업 (`SeatsGenerateService`)
- **커넥션 풀링**: HikariCP와 QueryDSL을 통한 쿼리 최적화
- **Redis 파이프라인**: 배치 캐시 업데이트를 위한 파이프라인 작업
- **무상태 서비스 설계**: 수평 확장을 위한 설계
- **데이터베이스 읽기 전용 복제본**: `JpaConfig`에 구성된 읽기 복제본 지원

## 개발 가이드라인

### 코딩 컨벤션
- **요청/응답 객체**: 컨트롤러 계층에서만 사용
- **DTO 패턴**: 서비스 간 통신
- **엔티티 검증**: 도메인 레벨에서 적절한 제약 조건
- **오류 처리**: `GlobalExceptionHandler`를 통한 커스텀 오류 코드
- 일관된 코드 스타일을 위한 기존 **네이밍 컨벤션** 준수

### 테스트 전략
- **단위 테스트**: 서비스 계층 비즈니스 로직 집중
- **동시성 테스트**: 레이스 컨디션 처리 검증 (`ReservationServiceConcurrencyTest`)
- **통합 테스트**: 전체 API 요청/응답 플로우 커버
- 데이터베이스 테스트를 위한 `@Transactional`과 `@DataJpaTest` 활용

## 개발 명령어

### Backend 명령어
```bash
# 전체 프로젝트 빌드
./gradlew build

# 개별 서비스 실행 (개발용)
./gradlew :ticket-service:bootRun
./gradlew :performance-service:bootRun  
./gradlew :user-service:bootRun

# 메인 애플리케이션 실행 (운영과 유사)
./gradlew :app:bootRun

# 테스트 실행
./gradlew test
./gradlew :ticket-service:test
./gradlew :performance-service:test
```

### Frontend 명령어
```bash
cd frontend
npm install     # 의존성 설치
npm run dev     # 개발 서버 (https://localhost:3573으로 프록시)
npm run build   # 운영 빌드
npm run lint    # ESLint 코드 품질 검사
```

## 대규모 트래픽 문제점 및 해결방안

### 문제점
1. **동시 접속자 대량 유입**: 티켓 오픈 시 순간적인 트래픽 집중
2. **좌석 중복 예매**: 같은 좌석에 대한 동시 예매 요청
3. **데이터베이스 부하**: 대량의 읽기/쓰기 요청
4. **시스템 응답 지연**: 높은 트래픽으로 인한 성능 저하

### 해결방안
1. **Redis 기반 대기열 시스템**: SortedSet을 활용한 순차 처리
2. **분산 락킹**: Redisson을 통한 좌석별 락 관리
3. **캐싱 전략**: Redis를 활용한 좌석 상태 캐싱
4. **비동기 처리**: 스케줄러를 통한 만료 처리 및 정리 작업
5. **커넥션 풀 최적화**: HikariCP를 통한 데이터베이스 연결 관리

## 보안 고려사항

- **SSL/TLS**: HTTPS 통신 강제
- **입력 검증**: 모든 사용자 입력에 대한 검증
- **SQL 인젝션 방지**: JPA/QueryDSL을 통한 안전한 쿼리 처리
- **세션 관리**: 안전한 사용자 세션 관리

이 문서는 FastTicketing 시스템의 전반적인 구조와 핵심 기능에 대한 개요를 제공합니다. 각 서비스의 상세한 API 문서와 구현 세부사항은 해당 모듈의 소스 코드를 참조하시기 바랍니다.