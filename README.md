# 무신사 상품 검색 시스템

## 문제 해석 및 구현 의도

### 1. 데이터 모델링 접근
- 브랜드와 카테고리는 1:1 관계로 설계 (한 브랜드당 카테고리별 단일 상품)
- Product 엔티티에 브랜드명, 카테고리, 가격을 포함하여 단순하고 효율적인 구조 채택
- Category를 enum으로 구현하여 타입 안정성과 확장성 확보

### 2. API 설계 원칙
1) **카테고리별 최저가 조회 API**
   - 사용자의 빠른 의사결정을 위해 모든 카테고리의 최저가를 한 번에 제공
   - 총액을 함께 제공하여 전체 구매 비용 파악 용이
   - 응답 형식은 프론트엔드 표현에 최적화

2) **단일 브랜드 최저가 조회 API**
   - 브랜드 충성도가 높은 고객을 위한 기능
   - 전체 카테고리 구매 시 최적의 브랜드 추천
   - 카테고리별 가격 세부 정보 제공으로 투명한 정보 제공

3) **카테고리별 가격 범위 조회 API**
   - 특정 카테고리 내 가격 스펙트럼 파악 가능
   - 최저가/최고가 브랜드 정보로 가격 대비 브랜드 가치 판단 지원

4) **상품 관리 API**
   - RESTful 원칙을 준수한 CRUD 설계
   - 데이터 무결성을 위한 철저한 유효성 검증
   - 명확한 에러 메시지로 운영 편의성 제고

### 3. 기술적 해결 방안
1) **성능 최적화**
   - JPA 쿼리 최적화로 복잡한 가격 비교 연산의 효율성 확보
   - 인메모리 DB 활용으로 빠른 응답 속도 보장
   - 페이지네이션 구현으로 대용량 데이터 처리 대비

2) **확장성 고려**
   - 새로운 브랜드/카테고리 추가가 용이한 구조
   - API 응답 구조의 표준화로 프론트엔드 연동 편의성 제공
   - Swagger 문서화로 API 이해도 및 테스트 용이성 확보

3) **에러 처리**
   - 예상 가능한 모든 예외 상황에 대한 체계적인 처리
   - 명확한 에러 메시지로 클라이언트 디버깅 지원
   - HTTP 상태 코드의 적절한 활용

### 4. 프론트엔드 구현 의도
- 직관적인 UI/UX로 사용자 경험 최적화
- 반응형 디자인으로 다양한 디바이스 지원
- 실시간 가격 비교 및 분석 기능 제공

### 5. 테스트 전략
- 단위 테스트로 각 컴포넌트의 독립적 기능 검증
- 통합 테스트로 전체 플로우 검증
- 엣지 케이스 처리 검증으로 안정성 확보

이러한 접근을 통해 무신사의 요구사항을 충실히 구현하면서도, 확장 가능하고 유지보수가 용이한 시스템을 구축하고자 했습니다.

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

## 프로젝트 구조
```
musinsa-assignment/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/musinsa/assignment/product/
│   │   │       ├── controller
│   │   │       ├── service
│   │   │       ├── repository
│   │   │       ├── domain
│   │   │       ├── dto
|   |   |       ├── frontend
│   │   │       ├── exception
│   │   │       ├── config
│   │   │       └── common
│   │   └── resources/
│   │       ├── application.yml
│   │       
│   └── test/
```

## API 명세

### 1. 카테고리별 최저가 조회
```
GET /api/lowest-price-by-category

Response:
{
    "success": true,
    "data": {
        "lowestPriceByCategory": [
            {
                "category": "상의",
                "brand": "C",
                "price": 10000
            },
            ...
        ],
        "totalPrice": 34100
    }
}
```

### 2. 단일 브랜드 최저가 조회
```
GET /api/lowest-price-single-brand

Response:
{
    "success": true,
    "data": {
        "brand": "D",
        "items": [
            {
                "category": "상의",
                "price": 10100
            },
            ...
        ],
        "totalPrice": 36100
    }
}
```

### 3. 카테고리별 가격 정보 조회
```
GET /api/category-price-info/{category}

Path Variables:
- category: Category enum (TOP, OUTER, ...)

Response:
{
    "success": true,
    "data": {
        "highest": {
            "brand": "I",
            "price": 11400
        },
        "lowest": {
            "brand": "C",
            "price": 10000
        }
    }
}
```

## 환경 설정
- Java Version: 17
- Spring Boot Version: 3.2.2
- Node.js Version: >= 18
- NPM Version: >= 9
- Gradle Version: 8.x

## 성능 최적화 전략

### 1. 데이터베이스 최적화
- 복합 인덱스 적용: (brandName, category)
- JPQL 최적화로 N+1 문제 방지
- 페이지네이션 적용으로 대용량 데이터 처리

### 2. API 응답 성능
- DTO 프로젝션으로 필요한 데이터만 조회
- 적절한 페이지 사이즈 설정 (기본값: 10)
- 추후 Redis 캐시 적용 고려


### 2. 일반적인 문제 해결
1. H2 Console 접속 실패
   - JDBC URL이 `jdbc:h2:mem:testdb`인지 확인
   - WebConfig의 CORS 설정 확인

2. Frontend API 호출 실패
   - 백엔드 서버 실행 상태 확인
   - next.config.ts의 proxy 설정 확인
   - 브라우저 콘솔의 CORS 에러 확인

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