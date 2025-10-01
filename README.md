# 구현 대상 빌딩 정보
### 기본 개요
- **빌딩 이름**: 토리 빌딩
- **층수**: 4층
- **주요 용도**: IT 교육
- **주요 사용 시간**: 월~금, 08:00 ~ 21:00 (주로 09:00 ~ 18:00에 사용)
- **특이사항**: 1개월/6개월 과정에 따라 피크 타임 불규칙

### 층별 상세 정보

| 층 | 주요 용도 | 상주 인원 | 컴퓨터 | 모니터 | 냉난방기 | 비고 |
|---|---|---|---|---|---|---|
| 1층 | 사무실 | 6명 | 7대 | 12대 | 2대 | 일일 방문객 10~50명 (불규칙) |
| 2층 | 강의실 | 26명 | 26대 | 26대 | 2대 | 2개 교실, 교실당 13명 |
| 3층 | 강의실 | 26명 | 26대 | 26대 | 2대 | 2개 교실, 교실당 13명 |
| 4층 | 강의실 | 13명 | 13대 | 13대 | 1대 | 1개 교실 |

### 공용 시설
- **화장실**: 모든 층에 남/녀 화장실 각 1개소
- **엘리베이터**: 1대
- **보일러**: 1대

</br>

# 테스트 계정
- ID: admin@zem.com
- PW: 1111

# 서버 API 기능 명세

## 1. API 설계 구조

- **Controller**: HTTP 요청을 받아 서비스 계층으로 비즈니스 로직 처리를 위임하고, 결과를 HTTP 응답으로 반환합니다.
- **Service**: 핵심 비즈니스 로직을 수행합니다. 여러 리포지토리를 조합하여 데이터를 처리할 수 있습니다.
- **Repository**: 데이터베이스와 직접 상호작용하며, JPA(Java Persistence API)를 통해 데이터의 CRUD(Create, Read, Update, Delete)를 담당합니다.
- **DTO (Data Transfer Object)**: 계층 간 데이터 전송을 위해 사용되는 객체입니다. API의 요청(Request) 및 응답(Response) 형식을 정의하며, Entity를 외부에 직접 노출하지 않도록 합니다.

---

## 2. API 상세 명세

### 2.1. 사용자 인증 (Authentication)

- **Controller**: `AuthController.java`
- **Service**: `AuthService.java`

| 기능 | HTTP Method | URL Path | Request Body/Params | Response Body | 설명 |
|---|---|---|---|---|---|
| 로그인 | `POST` | `/api/auth/login` | `LoginRequestDto` | `TokenResponseDto` | 사용자 로그인 처리 및 JWT 토큰 발급 |
| 로그아웃 | `POST` | `/api/auth/logout` | - (Header: Authorization) | `SuccessResponse` | 서버 측 토큰 무효화 (필요시 구현) |

<br>

### 2.2. 에너지 데이터 조회

- **Controller**: `EnergyDataController.java`
- **Service**: `EnergyDataService.java`

| 기능 | HTTP Method | URL Path | Request Body/Params | Response Body | 설명 |
|---|---|---|---|---|---|
| 기간별 에너지 데이터 조회 | `GET` | `/api/energy/summary` | `buildingId` (필수)<br>`unit` (hour, day, month, year)<br>`date` (조회 기준 날짜) | `EnergySummaryResponseDto` | 특정 빌딩의 지정된 기간(시/일/월/년) 단위의 전력, 수도, 가스 사용량, 요금, 탄소 배출량 조회 |

<br>

### 2.3. 에너지 통계 및 비교

- **Controller**: `EnergyStatController.java`
- **Service**: `EnergyStatService.java`

| 기능 | HTTP Method | URL Path | Request Body/Params | Response Body | 설명 |
|---|---|---|---|---|---|
| 요금 비교 데이터 조회 | `GET` | `/api/stats/comparison` | `buildingId` (필수)<br>`period` (year, day) | `ComparisonResponseDto` | 전국 평균, 상위 5% 평균, 우리 지역 평균 요금 비교 데이터 조회 |
| 에너지 효율 점수 조회 | `GET` | `/api/stats/score` | `buildingId` (필수) | `EnergyScoreResponseDto` | 건물의 에너지 효율 점수 조회 |

<br>

### 2.4. 알림

- **Controller**: `NotificationController.java`
- **Service**: `NotificationService.java`

| 기능 | HTTP Method | URL Path | Request Body/Params | Response Body | 설명 |
|---|---|---|---|---|---|
| 알림 목록 조회 | `GET` | `/api/notifications` | - (Header: Authorization) | `List<NotificationDto>` | 현재 사용자에 대한 모든 알림 목록 조회 |
| 알림 읽음 처리 | `POST` | `/api/notifications/{id}/read` | - (Header: Authorization) | `SuccessResponse` | 특정 알림을 읽음 상태로 변경 |

<br>

### 2.5. 예측 데이터 조회

- **Controller**: `PredictionController.java`
- **Service**: `PredictionService.java`

| 기능 | HTTP Method | URL Path | Request Body/Params | Response Body | 설명 |
|---|---|---|---|---|---|
| 예상 비용 조회 | `GET` | `/api/predictions/cost` | `buildingId` (필수)<br>`period` (month, year) | `PredictionResponseDto` | 금월 또는 금년의 예상 비용 조회 |
| 예상 탄소 배출량 조회 | `GET` | `/api/predictions/carbon` | `buildingId` (필수)<br>`period` (month, year) | `PredictionResponseDto` | 금월 또는 금년의 예상 탄소 배출량 조회 |

---

## 3. 실시간 데이터 (WebSocket)

- **WebSocket Handler**: `RealTimeDataHandler.java`
- **Service**: `RealTimeDataService.java` (스케줄링 포함)

| 기능 | 프로토콜 | Topic (Subscribe) | Message Payload | 설명 |
|---|---|---|---|---|
| 실시간 에너지 사용량 | WebSocket | `/topic/energy/{buildingId}` | `RealTimeDataDto` | 10초 주기로 장비/층별 실시간 전력, 수도, 가스 사용량 데이터 전송 |

- **데이터 저장**: `RealTimeDataService` 내에서 `@Scheduled` 어노테이션을 사용하여 5분마다 집계된 데이터를 `ElecConsumReading`, `WaterReading`, `GasReading` 테이블에 저장합니다.

</br>
</br>

# Spring Boot Backend - 개발 로드맵

## 1. 로그인 기능 (JWT 기반)

### 개요
- 별도의 회원가입 절차 없이, 사전에 데이터베이스에 등록된 사용자의 이메일과 비밀번호를 통해 로그인합니다.
- 로그인 성공 시, 서버는 JWT(JSON Web Token)를 발급하며, 클라이언트는 이후 API 요청 시 이 토큰을 사용하여 인증을 처리합니다.
- 서버는 세션을 사용하지 않는 상태 비저장(Stateless) 방식으로 동작합니다.

### 인증 흐름
1.  **로그인 요청**: 클라이언트가 `POST /api/auth/login`으로 이메일(id)과 비밀번호를 전송합니다.
2.  **자격 증명 확인**: `AuthService`에서 전달받은 이메일과 비밀번호를 `PasswordEncoder`를 통해 암호화된 DB의 비밀번호와 비교합니다.
3.  **JWT 발급**: 자격 증명이 유효하면, `JwtUtil.createToken()`을 통해 사용자의 이메일을 담은 Access Token을 생성합니다.
4.  **토큰 응답**: 생성된 토큰을 `TokenResponseDto`에 담아 클라이언트에게 반환합니다.
5.  **API 요청**: 클라이언트는 발급받은 토큰을 HTTP 헤더의 `Authorization` 필드에 `Bearer <token>` 형식으로 포함하여 API를 요청합니다.
6.  **토큰 검증**: `JwtAuthenticationFilter`가 모든 요청을 가로채 `Authorization` 헤더의 토큰을 검증합니다.
7.  **인증 처리**: 토큰이 유효하면, `SecurityContextHolder`에 인증 정보를 저장하여 해당 요청이 인증된 사용자의 요청임을 명시합니다.

## 2. 로그아웃 기능

### 개요
- JWT는 상태 비저장(Stateless) 특성상 서버에서 토큰을 직접 무효화하기 어렵습니다.
- 로그아웃은 클라이언트 측에서 저장된 토큰을 삭제하는 방식으로 처리하는 것을 기본으로 합니다.
- 서버는 `POST /api/auth/logout` 요청 시 성공 응답을 반환하여 클라이언트의 토큰 삭제 작업을 확인합니다.

## 3. 실시간 데이터 송신 (WebSocket)

### 개요
- 클라이언트는 실시간 에너지 사용량 데이터를 받기 위해 서버와 WebSocket 연결을 수립합니다.
- 서버는 특정 건물의 에너지 데이터를 구독하는 클라이언트에게 주기적으로 `RealTimeDataDto`를 전송합니다.

### 연결 흐름
1.  **WebSocket 핸드셰이크**: 클라이언트가 서버의 WebSocket 엔드포인트(예: `/ws`)로 연결을 요청합니다.
2.  **연결 수립**: 서버와 클라이언트 간의 WebSocket 연결이 성공적으로 수립됩니다.
3.  **Topic 구독**: 클라이언트는 특정 건물의 실시간 데이터를 받기 위해 `/topic/energy/{buildingId}` 토픽을 구독(Subscribe)합니다.
4.  **데이터 수신**: 서버의 `RealTimeDataService`는 스케줄링된 작업에 따라 10초마다 해당 토픽을 구독 중인 클라이언트에게 실시간 에너지 데이터를 전송합니다.

## 4. 실시간 데이터 모의 생성

### 개요
- 실제 센서 데이터가 없는 개발 환경을 위해, 실시간 사용 패턴과 유사한 에너지 데이터를 10초마다 생성합니다.
- 데이터 생성 시 `buildingInfo.txt` 파일의 정보를 기반으로 하며, 계절, 시간대, 특별 상황 등 다양한 변수를 고려하여 현실성을 높입니다.

### 데이터 생성 로직
1.  **기본 정보 로드**: `buildingInfo.txt` 파일을 파싱하여 빌딩의 층별 정보(사용 인원, 컴퓨터, 냉난방기, 조명 개수 등)를 메모리에 로드합니다.

2.  **전력 사용량 (층별)**:
    - **계산**: `(컴퓨터 수 * 사용 전력) + (냉난방기 수 * 사용 전력) + (조명 수 * 사용 전력)`을 기본 사용량으로 계산합니다.
    - **계절/시간 변수**: 여름/겨울철, 주간/야간에 따라 냉난방기 및 조명 사용률에 가중치를 적용합니다. (예: 여름 주간 - 냉방기 사용률 90%, 겨울 야간 - 조명 사용률 20%)

3.  **가스 사용량 (빌딩 전체)**:
    - **계산**: 빌딩 전체의 상주 인원을 기준으로 기본 사용량을 설정합니다.
    - **시간 변수**: 점심시간 등 특정 시간대에 사용량이 급증하는 패턴을 반영합니다.

4.  **수도 사용량 (층별)**:
    - **계산**: 각 층의 상주 인원을 기준으로 기본 사용량을 설정합니다.
    - **시간 변수**: 출근/퇴근/점심시간 등 인원 변동이 잦은 시간대에 사용량 변화를 반영합니다.

5.  **변동성 추가 (Randomness)**:
    - 모든 계산된 값에 일정 범위(예: ±5%)의 랜덤 노이즈를 추가하여 일관적이지 않은 실제 사용 패턴을 모방합니다.
    - 낮은 확률로 특정 이벤트(예: 회의로 인한 냉방기 추가 가동, 야근으로 인한 야간 전력 사용량 급증)를 시뮬레이션하여 데이터의 현실감을 더합니다.


