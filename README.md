# Chat Backend Service

채팅 기능을 제공하는 Spring Boot 백엔드 서비스입니다.

## 기술 스택

- Spring Boot 3.4.6
- Java 17
- MySQL
- Spring Data JPA
- WebSocket

## 환경 변수

다음 환경 변수들이 필요합니다:

- `CHAT_DB_URL`: 채팅 데이터베이스 URL
- `CHAT_DB_USER`: 채팅 데이터베이스 사용자명
- `CHAT_DB_PASSWORD`: 채팅 데이터베이스 비밀번호

## 로컬 실행

```bash
./gradlew bootRun
```

## Docker 빌드

```bash
docker build -t chat-backend .
```

## Kubernetes 배포

GitHub Actions를 통해 자동으로 배포됩니다. 다음 시크릿들이 필요합니다:

- `CHAT_DOCKER_IMAGE`: Docker 이미지 이름
- `CHAT_DB_URL`: 채팅 데이터베이스 URL
- `CHAT_DB_USER`: 채팅 데이터베이스 사용자명
- `CHAT_DB_PASSWORD`: 채팅 데이터베이스 비밀번호
- `DOCKER_USER`: Docker Hub 사용자명
- `DOCKER_PASSWORD`: Docker Hub 비밀번호
- `KUBE_CONFIG`: Kubernetes 설정

## API 엔드포인트

- 포트: 19092
- 헬스체크: `/actuator/health`
- 채팅 API: `/api/chat/*` 


배포: 202507281800-a