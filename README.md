# 중고 스마트폰 품질 판별 시스템 데이터베이스

## 개요

중고 스마트폰 이미지를 AI로 분석하여 품질 등급을 판별하는 시스템의 데이터베이스입니다.

## 설치 방법

```sql
-- SQL 파일 실행
source 테이블_생성_SQL.sql

-- 또는
mysql -u root -p < 테이블_생성_SQL.sql
```

## 테이블 구조 (간단 버전)

### 1. USER (사용자 테이블)

| 컬럼명   | 타입         | 설명            |
| -------- | ------------ | --------------- |
| user_id  | INT          | 사용자 ID (PK)  |
| email    | VARCHAR(100) | 이메일 (UNIQUE) |
| password | VARCHAR(255) | 비밀번호        |
| name     | VARCHAR(50)  | 이름            |

### 2. PRODUCT (상품 테이블)

| 컬럼명       | 타입         | 설명                  |
| ------------ | ------------ | --------------------- |
| product_id   | INT          | 상품 ID (PK)          |
| user_id      | INT          | 판매자 ID (FK → USER) |
| product_name | VARCHAR(100) | 상품명                |

### 3. IMAGE (이미지 테이블)

| 컬럼명     | 타입         | 설명                       |
| ---------- | ------------ | -------------------------- |
| image_id   | INT          | 이미지 ID (PK)             |
| product_id | INT          | 상품 ID (FK → PRODUCT)     |
| image_path | VARCHAR(500) | 이미지 경로                |
| image_type | VARCHAR(20)  | 이미지 유형(전면, 후면 등) |

### 4. GRADE (등급 테이블)

| 컬럼명     | 타입       | 설명                           |
| ---------- | ---------- | ------------------------------ |
| grade_id   | INT        | 등급 ID (PK)                   |
| grade_code | VARCHAR(1) | 등급 코드(A, B, C, D) (UNIQUE) |

### 5. INSPECTION (검수 작업 테이블)

| 컬럼명        | 타입        | 설명                       |
| ------------- | ----------- | -------------------------- |
| inspection_id | INT         | 검수 작업 ID (PK)          |
| product_id    | INT         | 상품 ID (FK → PRODUCT)     |
| status        | VARCHAR(20) | 작업 상태(대기중, 완료 등) |

### 6. RESULT (검수 결과 테이블)

| 컬럼명        | 타입 | 설명                           |
| ------------- | ---- | ------------------------------ |
| result_id     | INT  | 결과 ID (PK)                   |
| inspection_id | INT  | 검수 작업 ID (FK → INSPECTION) |
| grade_id      | INT  | 등급 ID (FK → GRADE)           |

### 7. DAMAGE (손상 정보 테이블)

| 컬럼명      | 타입        | 설명                         |
| ----------- | ----------- | ---------------------------- |
| damage_id   | INT         | 손상 ID (PK)                 |
| result_id   | INT         | 결과 ID (FK → RESULT)        |
| damage_type | VARCHAR(20) | 손상 유형(스크래치, 오염 등) |

## 테이블 관계

```
USER (1) ──< (N) PRODUCT
PRODUCT (1) ──< (N) IMAGE
PRODUCT (1) ── (1) INSPECTION
INSPECTION (1) ── (1) RESULT
RESULT (1) ──< (N) DAMAGE
RESULT (N) ──> (1) GRADE
```

## 주요 쿼리 예시

### 상품 검수 결과 조회

```sql
SELECT
    p.product_name,
    g.grade_code,
    d.damage_type
FROM PRODUCT p
JOIN INSPECTION i ON p.product_id = i.product_id
JOIN RESULT r ON i.inspection_id = r.inspection_id
JOIN GRADE g ON r.grade_id = g.grade_id
LEFT JOIN DAMAGE d ON r.result_id = d.result_id
WHERE p.product_id = 1;
```

### 등급별 통계

```sql
SELECT
    g.grade_code,
    COUNT(*) as count
FROM RESULT r
JOIN GRADE g ON r.grade_id = g.grade_id
GROUP BY g.grade_code;
```
