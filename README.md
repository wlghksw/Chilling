# 중고 스마트폰 품질 판별 시스템 데이터베이스

## 개요

이 데이터베이스는 중고 스마트폰의 이미지를 AI로 분석하여 품질 등급을 자동으로 판별하는 시스템의 데이터를 관리합니다.

## 데이터베이스 정보

- **데이터베이스명**: `smartphone_quality_db`
- **문자셋**: `utf8mb4`
- **콜레이션**: `utf8mb4_unicode_ci`
- **엔진**: `InnoDB`

## 설치 방법

### 1. 필수 요구사항

- MySQL 5.7 이상 또는 MariaDB 10.2 이상
- 데이터베이스 생성 및 테이블 생성 권한

### 2. 데이터베이스 생성

```bash
# MySQL 접속
mysql -u root -p

# SQL 파일 실행
source 테이블_생성_SQL.sql

# 또는 직접 실행
mysql -u root -p < 테이블_생성_SQL.sql
```

### 3. 확인

```sql
USE smartphone_quality_db;
SHOW TABLES;
```

## 테이블 구조

### 테이블 목록

1. **USER** - 사용자 정보 (판매자, 관리자)
2. **PRODUCT** - 상품 정보 (스마트폰)
3. **IMAGE** - 이미지 파일 정보
4. **GRADE** - 등급 기준 (A/B/C/D)
5. **INSPECTION** - 검수 작업
6. **RESULT** - 검수 결과
7. **DAMAGE** - 손상 상세 정보

### 테이블 상세 설명

#### 1. USER (사용자 테이블)

사용자 정보를 저장하는 테이블입니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| user_id | INT | PK, AUTO_INCREMENT | 사용자 고유 ID |
| user_type | VARCHAR(20) | NOT NULL, DEFAULT '판매자' | 사용자 유형(판매자, 관리자) |
| email | VARCHAR(100) | UNIQUE, NOT NULL | 이메일 주소 |
| password | VARCHAR(255) | NOT NULL | 비밀번호(암호화) |
| name | VARCHAR(50) | NOT NULL | 이름 |
| phone | VARCHAR(20) | NULL | 전화번호 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 가입일시 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 정보 수정일시 |

**인덱스:**
- `idx_user_email`: email 검색 최적화
- `idx_user_type`: user_type 검색 최적화

---

#### 2. PRODUCT (상품 테이블)

판매하려는 스마트폰의 기본 정보를 저장하는 테이블입니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| product_id | INT | PK, AUTO_INCREMENT | 상품 고유 ID |
| user_id | INT | FK, NOT NULL | 판매자 ID → USER(user_id) |
| product_name | VARCHAR(100) | NOT NULL | 상품명 |
| brand | VARCHAR(50) | NULL | 브랜드(삼성, 애플 등) |
| model | VARCHAR(50) | NULL | 모델명 |
| color | VARCHAR(20) | NULL | 색상 |
| release_year | INT | NULL | 출시 연도 |
| description | TEXT | NULL | 상품 설명 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 등록일시 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 수정일시 |

**외래키:**
- `user_id` → USER(user_id) ON DELETE CASCADE

**인덱스:**
- `idx_product_user`: 판매자별 상품 조회 최적화
- `idx_product_brand`: 브랜드별 검색 최적화
- `idx_product_model`: 모델별 검색 최적화

---

#### 3. IMAGE (이미지 테이블)

업로드된 스마트폰 이미지 파일의 정보를 저장하는 테이블입니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| image_id | INT | PK, AUTO_INCREMENT | 이미지 고유 ID |
| product_id | INT | FK, NOT NULL | 상품 ID → PRODUCT(product_id) |
| image_type | VARCHAR(20) | NOT NULL | 이미지 유형(전면, 후면, 측면, 모서리 등) |
| original_path | VARCHAR(500) | NOT NULL | 원본 이미지 저장 경로 |
| thumbnail_path | VARCHAR(500) | NULL | 썸네일 이미지 저장 경로 |
| file_size | INT | NULL | 파일 크기(바이트) |
| width | INT | NULL | 이미지 너비 |
| height | INT | NULL | 이미지 높이 |
| metadata | JSON | NULL | 이미지 메타데이터(JSON 형식) |
| uploaded_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 업로드 일시 |

**외래키:**
- `product_id` → PRODUCT(product_id) ON DELETE CASCADE

**인덱스:**
- `idx_image_product`: 상품별 이미지 조회 최적화
- `idx_image_type`: 이미지 유형별 검색 최적화

---

#### 4. GRADE (등급 테이블)

품질 등급 기준 정보를 저장하는 테이블입니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| grade_id | INT | PK, AUTO_INCREMENT | 등급 고유 ID |
| grade_code | VARCHAR(1) | UNIQUE, NOT NULL | 등급 코드(A, B, C, D) |
| grade_name | VARCHAR(20) | NOT NULL | 등급명 |
| description | TEXT | NULL | 등급 설명 |
| min_score | FLOAT | NOT NULL | 최소 점수 |
| max_score | FLOAT | NOT NULL | 최대 점수 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 생성일시 |

**등급 기준:**

| 등급 코드 | 등급명 | 최소 점수 | 최대 점수 | 설명 |
|-----------|--------|-----------|-----------|------|
| A | A등급 | 90.0 | 100.0 | 주요 손상 없음, 미세 스크래치 경미 |
| B | B등급 | 70.0 | 89.9 | 경미한 손상 1-2건, 화면 미세 흠집 |
| C | C등급 | 50.0 | 69.9 | 눈에 띄는 손상 다수, 후면/프레임 손상 |
| D | D등급 | 0.0 | 49.9 | 균열/깨짐, 프레임 변형, 기능 영향 가능 |

**인덱스:**
- `idx_grade_code`: 등급 코드 검색 최적화

---

#### 5. INSPECTION (검수 작업 테이블)

품질 검수 작업의 진행 상태를 관리하는 테이블입니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| inspection_id | INT | PK, AUTO_INCREMENT | 검수 작업 고유 ID |
| product_id | INT | FK, NOT NULL | 상품 ID → PRODUCT(product_id) |
| user_id | INT | FK, NOT NULL | 사용자 ID → USER(user_id) |
| status | VARCHAR(20) | NOT NULL, DEFAULT '대기중' | 작업 상태(대기중, 분석중, 완료, 실패) |
| started_at | DATETIME | NULL | 검수 시작 일시 |
| completed_at | DATETIME | NULL | 검수 완료 일시 |
| error_message | TEXT | NULL | 오류 메시지(실패 시) |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 생성일시 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 수정일시 |

**작업 상태 (status):**
- `대기중`: 검수 작업 대기 중
- `분석중`: AI 모델 분석 진행 중
- `완료`: 검수 작업 완료
- `실패`: 검수 작업 실패

**외래키:**
- `product_id` → PRODUCT(product_id) ON DELETE CASCADE
- `user_id` → USER(user_id) ON DELETE CASCADE

**인덱스:**
- `idx_inspection_product`: 상품별 검수 작업 조회
- `idx_inspection_user`: 사용자별 검수 작업 조회
- `idx_inspection_status`: 상태별 검색 최적화
- `idx_inspection_created`: 생성일시 기준 정렬 최적화

---

#### 6. RESULT (검수 결과 테이블)

품질 검수의 최종 결과 정보를 저장하는 테이블입니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| result_id | INT | PK, AUTO_INCREMENT | 결과 고유 ID |
| inspection_id | INT | FK, UNIQUE, NOT NULL | 검수 작업 ID → INSPECTION(inspection_id) |
| grade_id | INT | FK, NOT NULL | 등급 ID → GRADE(grade_id) |
| total_score | FLOAT | NULL | 종합 점수 |
| damage_count | INT | DEFAULT 0 | 검출된 손상 개수 |
| summary | TEXT | NULL | 결과 요약 |
| result_image_path | VARCHAR(500) | NULL | 결과 이미지 경로(하이라이트 처리된 이미지) |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 결과 생성 일시 |

**외래키:**
- `inspection_id` → INSPECTION(inspection_id) ON DELETE CASCADE
- `grade_id` → GRADE(grade_id)

**인덱스:**
- `idx_result_inspection`: 검수 작업별 결과 조회
- `idx_result_grade`: 등급별 결과 조회
- `idx_result_score`: 점수 기준 정렬 최적화

---

#### 7. DAMAGE (손상 정보 테이블)

검출된 손상의 상세 정보를 저장하는 테이블입니다.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| damage_id | INT | PK, AUTO_INCREMENT | 손상 고유 ID |
| result_id | INT | FK, NOT NULL | 결과 ID → RESULT(result_id) |
| damage_type | VARCHAR(20) | NOT NULL | 손상 유형(스크래치, 오염, 찍힘, 파손, 찌그러짐) |
| severity_score | FLOAT | NOT NULL | 심각도 점수(0.0 ~ 1.0) |
| area | FLOAT | NULL | 손상 면적 |
| count | INT | DEFAULT 1 | 손상 개수 |
| location | VARCHAR(20) | NULL | 손상 위치(전면, 후면, 측면 등) |
| coordinates | JSON | NULL | 손상 좌표 정보(JSON 형식) |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 생성일시 |

**손상 유형 (damage_type):**
- `스크래치`: 스크래치 손상
- `오염`: 오염/때/자국
- `찍힘`: 찍힘/덴트
- `파손`: 파손/균열/깨짐
- `찌그러짐`: 찌그러짐/변형

**손상 위치 (location):**
- `전면`: 전면 화면
- `후면`: 후면 유리
- `측면`: 측면 프레임
- `모서리`: 모서리 부분
- `기타`: 기타 위치

**외래키:**
- `result_id` → RESULT(result_id) ON DELETE CASCADE

**인덱스:**
- `idx_damage_result`: 결과별 손상 정보 조회
- `idx_damage_type`: 손상 유형별 검색 최적화
- `idx_damage_location`: 손상 위치별 검색 최적화

---

## 테이블 관계도

```
USER (1) ──< (N) PRODUCT
  │            │
  │            ├── (N) IMAGE
  │            │
  │            └── (1) INSPECTION ── (1) RESULT ── (1) GRADE
  │                                              │
  │                                              └── (N) DAMAGE
  │
  └──< (N) INSPECTION
```

### 주요 관계

1. **USER → PRODUCT**: 1:N (한 사용자가 여러 상품 등록 가능)
2. **USER → INSPECTION**: 1:N (한 사용자가 여러 검수 요청 가능)
3. **PRODUCT → IMAGE**: 1:N (한 상품에 여러 이미지 등록 가능)
4. **PRODUCT → INSPECTION**: 1:1 (한 상품당 한 번의 검수)
5. **INSPECTION → RESULT**: 1:1 (한 검수 작업당 하나의 결과)
6. **RESULT → DAMAGE**: 1:N (한 결과에 여러 손상 정보 포함)
7. **RESULT → GRADE**: N:1 (여러 결과가 하나의 등급에 속함)

---

## 초기 데이터

### 등급 데이터

SQL 파일 실행 시 다음 등급 데이터가 자동으로 삽입됩니다:

- **A등급**: 90.0 ~ 100.0 점
- **B등급**: 70.0 ~ 89.9 점
- **C등급**: 50.0 ~ 69.9 점
- **D등급**: 0.0 ~ 49.9 점

---

## 주요 쿼리 예시

### 1. 상품별 검수 결과 조회

```sql
SELECT 
    p.product_name,
    p.brand,
    p.model,
    r.total_score,
    g.grade_name,
    r.damage_count,
    r.created_at
FROM PRODUCT p
JOIN INSPECTION i ON p.product_id = i.product_id
JOIN RESULT r ON i.inspection_id = r.inspection_id
JOIN GRADE g ON r.grade_id = g.grade_id
WHERE p.product_id = 1;
```

### 2. 손상 유형별 통계

```sql
SELECT 
    d.damage_type,
    COUNT(*) as count,
    AVG(d.severity_score) as avg_severity,
    SUM(d.count) as total_damage_count
FROM DAMAGE d
JOIN RESULT r ON d.result_id = r.result_id
WHERE r.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY d.damage_type
ORDER BY count DESC;
```

### 3. 등급별 분포 통계

```sql
SELECT 
    g.grade_name,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM RESULT), 2) as percentage
FROM RESULT r
JOIN GRADE g ON r.grade_id = g.grade_id
GROUP BY g.grade_id, g.grade_name
ORDER BY g.grade_id;
```

### 4. 사용자별 검수 현황

```sql
SELECT 
    u.name,
    u.email,
    COUNT(i.inspection_id) as total_inspections,
    SUM(CASE WHEN i.status = '완료' THEN 1 ELSE 0 END) as completed_count
FROM USER u
LEFT JOIN INSPECTION i ON u.user_id = i.user_id
GROUP BY u.user_id, u.name, u.email;
```

---

## 백업 및 복구

### 백업

```bash
# 전체 데이터베이스 백업
mysqldump -u root -p smartphone_quality_db > backup.sql

# 특정 테이블만 백업
mysqldump -u root -p smartphone_quality_db USER PRODUCT > tables_backup.sql
```

### 복구

```bash
# 전체 데이터베이스 복구
mysql -u root -p smartphone_quality_db < backup.sql
```

---

## 주의사항

1. **외래키 제약조건**: 모든 외래키에 `ON DELETE CASCADE`가 설정되어 있어, 부모 레코드 삭제 시 자식 레코드도 자동으로 삭제됩니다.

2. **인덱스**: 검색 성능을 위해 주요 컬럼에 인덱스가 설정되어 있습니다.

3. **문자셋**: UTF-8 (utf8mb4)를 사용하여 한글 및 이모지 지원이 가능합니다.

4. **JSON 컬럼**: IMAGE.metadata와 DAMAGE.coordinates 컬럼은 JSON 형식을 사용합니다.

---

## 버전 정보

- **데이터베이스 버전**: MySQL 5.7+ / MariaDB 10.2+
- **최초 생성일**: 2024
- **마지막 업데이트**: 2024

---

## 문의

데이터베이스 관련 문의사항이 있으시면 개발팀에 연락주세요.

