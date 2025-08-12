-- MariaDB 초기 데이터 삽입
-- Spring Boot가 자동으로 실행합니다

-- 1) USERS
INSERT INTO users (login_id, nickname, email, password, real_name, phone, birth_year, gender,
                   profile_image, created_at, updated_at, is_active, is_admin)
VALUES
    ('admin', '관리자', 'admin@example.com', '1234', '홍관리', '010-0000-0000', 1990, 'male', NULL, NOW(), NOW(), TRUE, TRUE),
    ('user1', '유저1', 'user1@example.com', '1234', '김유저', '010-1111-1111', 1995, 'female', NULL, NOW(), NOW(), TRUE, FALSE),
    ('user2', '유저2', 'user2@example.com', '1234', '박유저', '010-2222-2222', 1992, 'male', NULL, NOW(), NOW(), TRUE, FALSE),
    ('user3', '유저3', 'user3@example.com', '1234', '이유저', '010-3333-3333', 1998, 'female', NULL, NOW(), NOW(), TRUE, FALSE);

-- 2) SPORTS
INSERT INTO sports (sport_name, sport_code, description, created_at)
VALUES
    ('축구', 'SOCCER', '축구 관련 카테고리', NOW()),
    ('풋살', 'FUTSAL', '풋살 관련 카테고리', NOW()),
    ('야구', 'BASEBALL', '야구 관련 카테고리', NOW()),
    ('테니스', 'TENNIS', '테니스 관련 카테고리', NOW());

-- 3) POST_CATEGORIES
INSERT INTO post_categories (category_name, category_type, sport_id, created_at)
VALUES
    ('자기소개', 'introduction', NULL, NOW()),
    ('커뮤니티', 'community', NULL, NOW()),
    ('공지사항', 'notice', NULL, NOW());

-- 4) POSTS
INSERT INTO posts (user_id, category_id, sport_id, title, content,
                   view_count, like_count, is_notice, created_at, updated_at, is_active)
VALUES
    ((SELECT user_id FROM users WHERE login_id='admin'), 
     (SELECT category_id FROM post_categories WHERE category_type='notice'),
     NULL, '환영합니다! 스포츠 커뮤니티에 오신 것을 환영합니다', '안녕하세요! 이곳은 스포츠를 좋아하는 분들이 모여서 정보를 공유하고 소통하는 공간입니다. 많은 참여 부탁드립니다!',
     100, 15, TRUE, NOW(), NOW(), TRUE),
     
    ((SELECT user_id FROM users WHERE login_id='user1'),
     (SELECT category_id FROM post_categories WHERE category_type='community'),
     (SELECT sport_id FROM sports WHERE sport_code='SOCCER'),
     '주말 축구 모임 모집합니다', '매주 토요일 오후 2시에 축구를 즐기고 싶은 분들을 모집합니다. 초보자도 환영합니다!',
     25, 8, FALSE, NOW(), NOW(), TRUE),
     
    ((SELECT user_id FROM users WHERE login_id='user2'),
     (SELECT category_id FROM post_categories WHERE category_type='community'),
     (SELECT sport_id FROM sports WHERE sport_code='BASKETBALL'),
     '농구 코트 추천', '서울 지역에서 농구를 즐길 수 있는 좋은 코트를 추천해드립니다. 실내/실외 모두 있습니다.',
     18, 5, FALSE, NOW(), NOW(), TRUE),
     
         ((SELECT user_id FROM users WHERE login_id='user3'),
     (SELECT category_id FROM post_categories WHERE category_type='community'),
     (SELECT sport_id FROM sports WHERE sport_code='BADMINTON'),
     '배드민턴 초보자 팁', '배드민턴을 처음 시작하는 분들을 위한 기본적인 팁을 공유합니다.',
     32, 12, FALSE, NOW(), NOW(), TRUE);

-- 5) REGIONS
INSERT INTO regions (region_name, region_code, cheonan_region_id, created_at)
VALUES
    ('천안시', 'CHEONAN', NULL, NOW()),
    ('아산시', 'ASAN', NULL, NOW()),
    ('공주시', 'GONGJU', NULL, NOW()),
    ('천안시 동남구', 'CHEONAN_DONGNAM', (SELECT region_id FROM regions WHERE region_code='CHEONAN'), NOW()),
    ('천안시 서북구', 'CHEONAN_SEOBUK', (SELECT region_id FROM regions WHERE region_code='CHEONAN'), NOW()),
    ('아산시 온양동', 'ASAN_ONYANG', (SELECT region_id FROM regions WHERE region_code='ASAN'), NOW()),
    ('아산시 배방동', 'ASAN_BAEBANG', (SELECT region_id FROM regions WHERE region_code='ASAN'), NOW()),
    ('공주시 신관동', 'GONGJU_SINGWAN', (SELECT region_id FROM regions WHERE region_code='GONGJU'), NOW()),
    ('공주시 웅진동', 'GONGJU_WOONGJIN', (SELECT region_id FROM regions WHERE region_code='GONGJU'), NOW());

-- 6) VENUES
INSERT INTO venues (venue_name, sport_id, region_id, address, phone, description, venue_image, latitude, longitude, created_at, updated_at)
VALUES
    ('천안축구공원', (SELECT sport_id FROM sports WHERE sport_code='SOCCER'), 
     (SELECT region_id FROM regions WHERE region_code='CHEONAN_DONGNAM'),
     '천안시 동남구 축구로 123', '041-123-4567', '11인제 축구장, 인조잔디, 야간조명 완비', 
     'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=800', 36.8151, 127.1139, NOW(), NOW()),
     
    ('천안풋살장', (SELECT sport_id FROM sports WHERE sport_code='FUTSAL'),
     (SELECT region_id FROM regions WHERE region_code='CHEONAN_SEOBUK'),
     '천안시 서북구 풋살로 456', '041-234-5678', '5인제 풋살장, 실내 코트, 야간조명 완비',
     'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=800', 36.8167, 127.1122, NOW(), NOW()),
     
    ('아산야구장', (SELECT sport_id FROM sports WHERE sport_code='BASEBALL'),
     (SELECT region_id FROM regions WHERE region_code='ASAN_ONYANG'),
     '아산시 온양동 야구로 654', '041-567-8901', '9인제 야구장, 관중석 500석',
     'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=800', 36.7856, 127.0056, NOW(), NOW()),
     
    ('공주테니스장', (SELECT sport_id FROM sports WHERE sport_code='TENNIS'),
     (SELECT region_id FROM regions WHERE region_code='GONGJU_SINGWAN'),
     '공주시 신관동 테니스로 789', '041-345-6789', '테니스장 4면, 실내/실외 코트',
     'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=800', 36.4567, 127.1256, NOW(), NOW()),
     
    ('천안축구센터', (SELECT sport_id FROM sports WHERE sport_code='SOCCER'),
     (SELECT region_id FROM regions WHERE region_code='CHEONAN_SEOBUK'),
     '천안시 서북구 축구센터로 789', '041-456-7890', '7인제 축구장, 인조잔디, 실내 코트',
     'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=800', 36.8178, 127.1133, NOW(), NOW()),
     
    ('아산풋살센터', (SELECT sport_id FROM sports WHERE sport_code='FUTSAL'),
     (SELECT region_id FROM regions WHERE region_code='ASAN_BAEBANG'),
     '아산시 배방동 풋살센터로 321', '041-789-0123', '5인제 풋살장, 실내 코트, 주차장 완비',
     'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=800', 36.7878, 127.0078, NOW(), NOW()),
     
    ('공주야구장', (SELECT sport_id FROM sports WHERE sport_code='BASEBALL'),
     (SELECT region_id FROM regions WHERE region_code='GONGJU_WOONGJIN'),
     '공주시 웅진동 야구로 147', '041-890-1234', '9인제 야구장, 관중석 300석, 야간조명',
     'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=800', 36.4578, 127.1267, NOW(), NOW()),
     
    ('천안테니스클럽', (SELECT sport_id FROM sports WHERE sport_code='TENNIS'),
     (SELECT region_id FROM regions WHERE region_code='CHEONAN_DONGNAM'),
     '천안시 동남구 테니스로 258', '041-012-3456', '테니스장 6면, 실내/실외 코트, 클럽하우스',
     'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=800', 36.8156, 127.1145, NOW(), NOW());

-- 7) MATCHES
INSERT INTO matches (venue_id, sport_id, match_date, start_time, end_time, max_players, current_players, 
                     description, status, created_by, created_at, updated_at)
VALUES
    -- 오늘 매치 (즉시 참여 가능)
    ((SELECT venue_id FROM venues WHERE venue_name='천안축구공원'),
     (SELECT sport_id FROM sports WHERE sport_code='SOCCER'),
     CURDATE(), '20:00:00', '22:00:00', 22, 0,
     '오늘 밤 축구 모임입니다! 야간조명 완비, 즉시 참여 가능합니다.', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user1'), NOW(), NOW()),
     
    -- 내일 매치
    ((SELECT venue_id FROM venues WHERE venue_name='천안풋살장'),
     (SELECT sport_id FROM sports WHERE sport_code='FUTSAL'),
     DATE_ADD(CURDATE(), INTERVAL 1 DAY), '19:00:00', '21:00:00', 10, 0,
     '내일 저녁 풋살 모임입니다. 실력 상관없이 참여 가능합니다.', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user2'), NOW(), NOW()),
     
    -- 이번 주 매치들
    ((SELECT venue_id FROM venues WHERE venue_name='아산야구장'),
     (SELECT sport_id FROM sports WHERE sport_code='BASEBALL'),
     DATE_ADD(CURDATE(), INTERVAL 2 DAY), '13:00:00', '16:00:00', 18, 0,
     '이번 주 야구 모임입니다. 글러브 대여 가능합니다.', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user3'), NOW(), NOW()),
     
    ((SELECT venue_id FROM venues WHERE venue_name='공주테니스장'),
     (SELECT sport_id FROM sports WHERE sport_code='TENNIS'),
     DATE_ADD(CURDATE(), INTERVAL 3 DAY), '10:00:00', '12:00:00', 4, 0,
     '이번 주 주말 오전 테니스 모임입니다. 라켓 대여 가능합니다.', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user1'), NOW(), NOW()),
     
    -- 다음 주 매치들
    ((SELECT venue_id FROM venues WHERE venue_name='천안축구공원'),
     (SELECT sport_id FROM sports WHERE sport_code='SOCCER'),
     DATE_ADD(CURDATE(), INTERVAL 7 DAY), '14:00:00', '16:00:00', 22, 0,
     '다음 주 주말 축구 모임입니다. 초보자도 환영합니다!', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user2'), NOW(), NOW()),
     
    ((SELECT venue_id FROM venues WHERE venue_name='천안풋살장'),
     (SELECT sport_id FROM sports WHERE sport_code='FUTSAL'),
     DATE_ADD(CURDATE(), INTERVAL 8 DAY), '20:00:00', '22:00:00', 10, 0,
     '다음 주 저녁 풋살 모임입니다. 야간 조명 완비!', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user3'), NOW(), NOW()),
     
    ((SELECT venue_id FROM venues WHERE venue_name='아산야구장'),
     (SELECT sport_id FROM sports WHERE sport_code='BASEBALL'),
     DATE_ADD(CURDATE(), INTERVAL 9 DAY), '13:00:00', '16:00:00', 18, 0,
     '다음 주 야구 모임입니다. 글러브 대여 가능합니다.', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user1'), NOW(), NOW()),
     
    ((SELECT venue_id FROM venues WHERE venue_name='공주테니스장'),
     (SELECT sport_id FROM sports WHERE sport_code='TENNIS'),
     DATE_ADD(CURDATE(), INTERVAL 10 DAY), '16:00:00', '18:00:00', 4, 0,
     '다음 주 오후 테니스 모임입니다. 중급자 이상 환영!', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user2'), NOW(), NOW()),
     
    -- 추가 구장들의 매치
    ((SELECT venue_id FROM venues WHERE venue_name='천안축구센터'),
     (SELECT sport_id FROM sports WHERE sport_code='SOCCER'),
     DATE_ADD(CURDATE(), INTERVAL 1 DAY), '14:00:00', '16:00:00', 14, 0,
     '내일 오후 7인제 축구 모임입니다. 실내 코트라 비 오는 날도 가능!', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user1'), NOW(), NOW()),
     
    ((SELECT venue_id FROM venues WHERE venue_name='아산풋살센터'),
     (SELECT sport_id FROM sports WHERE sport_code='FUTSAL'),
     DATE_ADD(CURDATE(), INTERVAL 2 DAY), '20:00:00', '22:00:00', 10, 0,
     '이번 주 저녁 풋살 모임입니다. 주차장 완비되어 있습니다!', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user2'), NOW(), NOW()),
     
    ((SELECT venue_id FROM venues WHERE venue_name='공주야구장'),
     (SELECT sport_id FROM sports WHERE sport_code='BASEBALL'),
     DATE_ADD(CURDATE(), INTERVAL 4 DAY), '15:00:00', '18:00:00', 18, 0,
     '이번 주 주말 야구 모임입니다. 야간조명 완비!', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user3'), NOW(), NOW()),
     
    ((SELECT venue_id FROM venues WHERE venue_name='천안테니스클럽'),
     (SELECT sport_id FROM sports WHERE sport_code='TENNIS'),
     DATE_ADD(CURDATE(), INTERVAL 5 DAY), '09:00:00', '11:00:00', 4, 0,
     '이번 주 주말 오전 테니스 모임입니다. 클럽하우스 이용 가능!', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user1'), NOW(), NOW()),
     
    -- 추가 매치들 (다양한 상태와 참가자 수)
    ((SELECT venue_id FROM venues WHERE venue_name='천안축구공원'),
     (SELECT sport_id FROM sports WHERE sport_code='SOCCER'),
     DATE_ADD(CURDATE(), INTERVAL 11 DAY), '15:00:00', '17:00:00', 22, 15,
     '다다음 주 주말 축구 모임입니다. 이미 15명이 참가했습니다!', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user2'), NOW(), NOW()),
     
    ((SELECT venue_id FROM venues WHERE venue_name='천안풋살장'),
     (SELECT sport_id FROM sports WHERE sport_code='FUTSAL'),
     DATE_ADD(CURDATE(), INTERVAL 12 DAY), '19:00:00', '21:00:00', 10, 10,
     '다다음 주 저녁 풋살 모임입니다. 마감되었습니다!', 'FULL',
     (SELECT user_id FROM users WHERE login_id='user3'), NOW(), NOW()),
     
    ((SELECT venue_id FROM venues WHERE venue_name='아산야구장'),
     (SELECT sport_id FROM sports WHERE sport_code='BASEBALL'),
     DATE_ADD(CURDATE(), INTERVAL 13 DAY), '14:00:00', '17:00:00', 18, 8,
     '다다음 주 야구 모임입니다. 아직 10자리 남았습니다!', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user1'), NOW(), NOW()),
     
    ((SELECT venue_id FROM venues WHERE venue_name='공주테니스장'),
     (SELECT sport_id FROM sports WHERE sport_code='TENNIS'),
     DATE_ADD(CURDATE(), INTERVAL 14 DAY), '11:00:00', '13:00:00', 4, 2,
     '다다음 주 오후 테니스 모임입니다. 2자리 남았습니다!', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user2'), NOW(), NOW()),
     
    -- 이번 달 말 매치들
    ((SELECT venue_id FROM venues WHERE venue_name='천안축구센터'),
     (SELECT sport_id FROM sports WHERE sport_code='SOCCER'),
     DATE_ADD(CURDATE(), INTERVAL 15 DAY), '16:00:00', '18:00:00', 14, 12,
     '이번 달 말 7인제 축구 모임입니다. 2자리 남았습니다!', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user3'), NOW(), NOW()),
     
    ((SELECT venue_id FROM venues WHERE venue_name='아산풋살센터'),
     (SELECT sport_id FROM sports WHERE sport_code='FUTSAL'),
     DATE_ADD(CURDATE(), INTERVAL 16 DAY), '21:00:00', '23:00:00', 10, 0,
     '이번 달 말 늦은 밤 풋살 모임입니다. 야간 조명 완비!', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user1'), NOW(), NOW()),
     
    ((SELECT venue_id FROM venues WHERE venue_name='공주야구장'),
     (SELECT sport_id FROM sports WHERE sport_code='BASEBALL'),
     DATE_ADD(CURDATE(), INTERVAL 17 DAY), '12:00:00', '15:00:00', 18, 0,
     '이번 달 말 점심 시간 야구 모임입니다. 점심 도시락 준비해주세요!', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user2'), NOW(), NOW()),
     
    ((SELECT venue_id FROM venues WHERE venue_name='천안테니스클럽'),
     (SELECT sport_id FROM sports WHERE sport_code='TENNIS'),
     DATE_ADD(CURDATE(), INTERVAL 18 DAY), '08:00:00', '10:00:00', 4, 1,
     '이번 달 말 이른 아침 테니스 모임입니다. 상쾌한 아침을 시작해보세요!', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user3'), NOW(), NOW()),
     
    -- 다음 달 초 매치들
    ((SELECT venue_id FROM venues WHERE venue_name='천안축구공원'),
     (SELECT sport_id FROM sports WHERE sport_code='SOCCER'),
     DATE_ADD(CURDATE(), INTERVAL 20 DAY), '13:00:00', '15:00:00', 22, 0,
     '다음 달 초 주말 축구 모임입니다. 새해 첫 축구 어떠세요?', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user1'), NOW(), NOW()),
     
    ((SELECT venue_id FROM venues WHERE venue_name='천안풋살장'),
     (SELECT sport_id FROM sports WHERE sport_code='FUTSAL'),
     DATE_ADD(CURDATE(), INTERVAL 21 DAY), '18:00:00', '20:00:00', 10, 0,
     '다음 달 초 저녁 풋살 모임입니다. 실력 상관없이 참여 가능합니다!', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user2'), NOW(), NOW()),
     
    ((SELECT venue_id FROM venues WHERE venue_name='아산야구장'),
     (SELECT sport_id FROM sports WHERE sport_code='BASEBALL'),
     DATE_ADD(CURDATE(), INTERVAL 22 DAY), '15:00:00', '18:00:00', 18, 0,
     '다음 달 초 야구 모임입니다. 글러브와 배트 대여 가능합니다!', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user3'), NOW(), NOW()),
     
    ((SELECT venue_id FROM venues WHERE venue_name='공주테니스장'),
     (SELECT sport_id FROM sports WHERE sport_code='TENNIS'),
     DATE_ADD(CURDATE(), INTERVAL 23 DAY), '10:00:00', '12:00:00', 4, 0,
     '다음 달 초 오전 테니스 모임입니다. 라켓 대여 가능합니다!', 'OPEN',
     (SELECT user_id FROM users WHERE login_id='user1'), NOW(), NOW());
