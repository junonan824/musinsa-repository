# 무신사 상품 검색 시스템

이 프로젝트는 의류 브랜드별 상품 가격을 분석하는 애플리케이션입니다. 주요 구현 범위는 다음과 같습니다:

### 백엔드 (Spring Boot)

#### 프로젝트 구조
- Controller-Service-Repository 계층 구조
- Domain, DTO, Exception 등 명확한 패키지 구조
- 글로벌 예외 처리 구현

1. **데이터 초기화**
- 9개 브랜드(A-I)의 8개 카테고리별 가격 데이터 초기화
- H2 인메모리 데이터베이스 사용

2. **REST API 엔드포인트**
```markdown
- GET /api/products: 전체 상품 목록 조회 (페이지네이션)
- GET /api/lowest-price-by-category: 카테고리별 최저가 상품 조회
- GET /api/lowest-price-single-brand: 단일 브랜드 최저가격 조회
- GET /api/category-price-info/{category}: 특정 카테고리의 최고/최저가 조회
```

3. **예외 처리**
- 글로벌 예외 처리 구현
- 404, 400 등 HTTP 상태 코드 적절히 반환


5. **테스트 커버리지**
- 단위 테스트와 통합 테스트 구현
- Controller, Service 레벨 테스트
- JPA Repository 테스트

6. **데이터 접근**
- JPA Repository 최적화된 쿼리 메소드
- JPQL을 활용한 복잡한 쿼리 구현

### 프론트엔드 (Next.js)

1. **상품 목록 화면**
- 전체 상품 목록 표시
- 페이지네이션 구현
- 브랜드, 카테고리, 가격 정보 표시

2. **가격 분석 기능**
- 카테고리별 최고/최저가 분석
- 브랜드별 가격 비교
- 시각적 데이터 표현

3. **사용자 인터페이스**
- Tailwind CSS를 활용한 반응형 디자인
- 직관적인 UI/UX 구현

### 기술 스택
```markdown
- Backend: Spring Boot, JPA, H2 Database
- API 문서화: Swagger
- Frontend: Next.js, TypeScript, Tailwind CSS
- 개발 도구: Gradle, npm
```

### 주요 기능
1. 카테고리별 최저가 브랜드와 상품 가격 조회
2. 단일 브랜드로 모든 카테고리 상품 구매시 최저가격 조회
3. 특정 카테고리의 최고/최저가 브랜드와 가격 조회

이 구현을 통해 사용자는 브랜드별, 카테고리별 가격을 쉽게 비교하고 분석할 수 있습니다.

----
## 백엔드 실행 방법 (Spring Boot)

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

### 개발 도구 접속

#### H2 Database Console
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (빈 값)

#### Swagger UI (API 문서)
- URL: `http://localhost:8080/swagger-ui.html`
- API 엔드포인트 테스트 및 문서 확인 가능