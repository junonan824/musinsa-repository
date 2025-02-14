# 무신사 상품 검색 시스템

이 프로젝트는 Spring Boot 백엔드와 Next.js 프론트엔드로 구성되어 있습니다. 다음과 같이 실행할 수 있습니다:

## 백엔드 실행 (Spring Boot)

1. Gradle을 사용하여 빌드 및 실행:
```bash
# 프로젝트 루트 디렉토리에서
./gradlew build
./gradlew bootRun
```
또는 IDE(IntelliJ IDEA 등)에서 직접 실행:
- `ProductApiApplication.java` 파일을 열어서 실행

백엔드는 기본적으로 `http://localhost:8080`에서 실행됩니다.

### 프론트엔드 실행 (Next.js)

1. frontend 디렉토리로 이동:
```bash
cd src/main/java/com/musinsa/assignment/product/frontend
```

2. 의존성 설치:
```bash
npm install
# 또는
yarn install
```

3. 개발 서버 실행:
```bash
npm run dev
# 또는
yarn dev
```

프론트엔드는 기본적으로 `http://localhost:3000`에서 실행됩니다.

### 전체 시스템 실행 순서

1. 먼저 백엔드 서버를 실행합니다 (8080 포트)
2. 그 다음 프론트엔드 개발 서버를 실행합니다 (3000 포트)
3. 웹 브라우저에서 `http://localhost:3000`으로 접속

### 주의사항

- `WebConfig.java`에서 CORS 설정이 되어 있어 프론트엔드(3000)에서 백엔드(8080)로의 API 요청이 가능합니다.
- 데이터베이스는 H2 인메모리 데이터베이스를 사용하며, `ProductInitializer.java`에서 초기 데이터가 자동으로 로드됩니다.
- 프론트엔드 개발 시에는 `next.config.ts`의 리라이트 설정을 통해 API 요청이 백엔드로 프록시됩니다.
