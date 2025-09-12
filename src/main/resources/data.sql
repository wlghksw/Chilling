-- 기존 데이터 완전 삭제 (순서 중요: 외래키 제약조건 고려)
SET FOREIGN_KEY_CHECKS = 0;

-- 모든 테이블 데이터 삭제
TRUNCATE TABLE match_applications;
TRUNCATE TABLE matches;
TRUNCATE TABLE users;
TRUNCATE TABLE venues;
TRUNCATE TABLE sports;
TRUNCATE TABLE regions;
TRUNCATE TABLE myposts;
TRUNCATE TABLE mypost_comments;
TRUNCATE TABLE mypost_likes;
TRUNCATE TABLE mypost_media;
TRUNCATE TABLE community_posts;
TRUNCATE TABLE team_members;
TRUNCATE TABLE teams;

-- 추가 강제 삭제
DELETE FROM match_applications WHERE 1=1;
DELETE FROM matches WHERE 1=1;
DELETE FROM users WHERE 1=1;
DELETE FROM venues WHERE 1=1;
DELETE FROM sports WHERE 1=1;
DELETE FROM regions WHERE 1=1;
DELETE FROM myposts WHERE 1=1;
DELETE FROM mypost_comments WHERE 1=1;
DELETE FROM mypost_likes WHERE 1=1;
DELETE FROM mypost_media WHERE 1=1;
DELETE FROM community_posts WHERE 1=1;
DELETE FROM team_members WHERE 1=1;
DELETE FROM teams WHERE 1=1;

SET FOREIGN_KEY_CHECKS = 1;

-- AUTO_INCREMENT 초기화
ALTER TABLE match_applications AUTO_INCREMENT = 1;
ALTER TABLE matches AUTO_INCREMENT = 1;
ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE venues AUTO_INCREMENT = 1;
ALTER TABLE sports AUTO_INCREMENT = 1;
ALTER TABLE regions AUTO_INCREMENT = 1;
ALTER TABLE myposts AUTO_INCREMENT = 1;
ALTER TABLE mypost_comments AUTO_INCREMENT = 1;
ALTER TABLE mypost_likes AUTO_INCREMENT = 1;
ALTER TABLE mypost_media AUTO_INCREMENT = 1;
ALTER TABLE community_posts AUTO_INCREMENT = 1;
ALTER TABLE team_members AUTO_INCREMENT = 1;
ALTER TABLE teams AUTO_INCREMENT = 1;

-- MariaDB 초기 데이터 삽입
-- Spring Boot가 자동으로 실행합니다

-- 1) USERS (최소한의 필수 사용자만)
INSERT INTO users (login_id, nickname, email, password, real_name, phone, birth_year, gender,
                   profile_image, created_at, updated_at, is_active, is_admin)
VALUES
    ('admin', '관리자', 'admin@example.com', '1234', '홍관리', '010-0000-0000', 1990, 'male', NULL, NOW(), NOW(), TRUE, TRUE);

-- 2) SPORTS (필수 스포츠만)
INSERT INTO sports (sport_name, sport_code, description, created_at)
VALUES
    ('축구', 'SOCCER', '축구 관련 카테고리', NOW()),
    ('풋살', 'FUTSAL', '풋살 관련 카테고리', NOW()),
    ('야구', 'BASEBALL', '야구 관련 카테고리', NOW()),
    ('테니스', 'TENNIS', '테니스 관련 카테고리', NOW());

-- 3) POST_CATEGORIES (필수 카테고리만)
INSERT INTO post_categories (category_name, category_type, sport_id, created_at)
VALUES
    ('자기소개', 'introduction', NULL, NOW()),
    ('공지사항', 'NOTICE', NULL, NOW()),
    ('자유게시판', 'COMMUNITY', NULL, NOW());

-- 4) REGIONS (필수 지역만)
INSERT INTO regions (region_name, region_code, cheonan_region_id, created_at)
VALUES
    ('천안시 동남구', 'CHEONAN_DONGNAM', NULL, NOW()),
    ('천안시 서북구', 'CHEONAN_SEOBUK', NULL, NOW());

-- 5) VENUES (모든 스포츠 구장 포함)
INSERT INTO venues (venue_name, sport_id, region_id, address, phone, description, venue_image, latitude, longitude, created_at, updated_at)
VALUES
    ('천안축구공원', (SELECT sport_id FROM sports WHERE sport_code='SOCCER'),
     (SELECT region_id FROM regions WHERE region_code='CHEONAN_DONGNAM'),
     '천안시 동남구 축구로 123', '041-123-4567', '11인제 축구장, 인조잔디, 야간조명 완비',
     '/uploads/images/soccer.png', 36.8151, 127.1139, NOW(), NOW()),

    ('천안풋살장', (SELECT sport_id FROM sports WHERE sport_code='FUTSAL'),
     (SELECT region_id FROM regions WHERE region_code='CHEONAN_SEOBUK'),
     '천안시 동나구 풋사로 555', '041-234-5678', '5인제 풋살장, 실내 코트, 야간조명 완비',
     '/uploads/images/futsal.png', 36.8167, 127.1122, NOW(), NOW()),

    ('천안야구장', (SELECT sport_id FROM sports WHERE sport_code='BASEBALL'),
     (SELECT region_id FROM regions WHERE region_code='CHEONAN_DONGNAM'),
     '천안시 동남구 야구로 789', '041-345-6789', '9인제 야구장, 관중석 300석, 야간조명 완비',
     '/uploads/images/bb.png', 36.8156, 127.1145, NOW(), NOW()),

    ('천안테니스장', (SELECT sport_id FROM sports WHERE sport_code='TENNIS'),
     (SELECT region_id FROM regions WHERE region_code='CHEONAN_SEOBUK'),
     '천안시 서북구 테니스로 321', '041-456-7890', '테니스장 4면, 실내/실외 코트, 주차장 완비',
     '/uploads/images/tennis.png', 36.8165, 127.1130, NOW(), NOW());

