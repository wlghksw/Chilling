package com.example.users.service;

import com.example.users.entity.Posts;
import com.example.users.repository.PostsRepository;
import com.example.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostsService {

    private final UsersRepository usersRepository;
    private final PostsRepository postsRepository;
    private final JdbcTemplate jdbcTemplate;



    public Posts getPostOrThrow(Integer id) {
        return postsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 글이 없습니다. id=" + id));
    }


    @Transactional
    public void increaseViewCount(Integer postId) {
        int updated = postsRepository.increaseViewCount(postId);
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 글이 없습니다. id=" + postId);
        }
    }

    @Transactional
    public Integer createBasic(Integer userId, String title, String content,
                               Integer sportId, Integer categoryId) {

        // --- 0) 기본 검증 ---
        if (userId == null || !usersRepository.existsById(Long.valueOf(userId))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 user_id: " + userId);
        }
        if (categoryId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "categoryId는 필수입니다.");
        }
        if (title == null || title.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "제목을 입력하세요.");
        }
        if (content == null || content.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "내용을 입력하세요.");
        }

        log.info("CREATE POST REQ => userId={}, sportId={}, categoryId={}, title='{}'",
                userId, sportId, categoryId, title);

        // --- 1) 부모 보장 (없으면 즉시 생성, 있으면 NO-OP) ---
        // post_categories는 category_name, category_type, sport_id가 필요
        ensureCategoryExists(categoryId, sportId);
        // sports는 sport_name, sport_code가 필요 (sportId가 있을 때만)
        if (sportId != null) ensureSportExists(sportId);

        // --- 2) posts 저장 ---
        Posts p = new Posts();
        p.setUser_id(userId);
        p.setCategory_id(categoryId);
        p.setSport_id(sportId); // NULL 가능
        p.setTitle(title);
        p.setContent(content);
        // @PrePersist / @PreUpdate 로 기본값 처리 권장 (view_count=0 등)

        Posts saved = postsRepository.save(p);
        log.info("POST CREATE OK => post_id={}, userId={}, sportId={}, categoryId={}",
                saved.getPost_id(), userId, sportId, categoryId);
        return saved.getPost_id();
    }

    /** post_categories(category_id) 없으면 즉시 생성 (스키마 강제 충족) */
    private void ensureCategoryExists(Integer categoryId, Integer sportId) {
        // 네 고정 규칙에 맞게 이름/타입 매핑
        final String categoryName;
        final String categoryType; // ENUM('introduction','community','notice')

        switch (categoryId) {
            case 1 -> { categoryName = "내소개";   categoryType = "introduction"; }
            case 2 -> { categoryName = "커뮤니티"; categoryType = "community"; }
            case 3 -> { categoryName = "구장안내"; categoryType = "community"; }
            case 4 -> { categoryName = "매칭신청"; categoryType = "notice"; }
            default -> { categoryName = "카테고리" + categoryId; categoryType = "community"; }
        }

        if (sportId != null) ensureSportExists(sportId);


        jdbcTemplate.update(
                "INSERT INTO post_categories " +
                        "  (category_id, category_name, category_type, sport_id, created_at) " +
                        "VALUES (?, ?, ?, ?, NOW()) " +
                        "ON DUPLICATE KEY UPDATE category_id = category_id",
                categoryId, categoryName, categoryType, sportId
        );
    }

    /** sports(sport_id) 없으면 즉시 생성 (스키마 강제 충족: sport_name, sport_code 필요) */
    private void ensureSportExists(Integer sportId) {
        final String sportName;
        final String sportCode; // UNIQUE NOT NULL

        switch (sportId) {
            case 1 -> { sportName = "축구";   sportCode = "SOCCER"; }
            case 2 -> { sportName = "풋살";   sportCode = "FUTSAL"; }
            case 3 -> { sportName = "야구";   sportCode = "BASEBALL"; }
            case 4 -> { sportName = "테니스"; sportCode = "TENNIS"; }
            default -> { sportName = "스포츠" + sportId; sportCode = "SPORT_" + sportId; }
        }

        // 이미 존재하면 NO-OP, 없으면 INSERT
        jdbcTemplate.update(
                "INSERT INTO sports (sport_id, sport_name, sport_code, created_at) " +
                        "VALUES (?, ?, ?, NOW()) " +
                        "ON DUPLICATE KEY UPDATE sport_id = sport_id",
                sportId, sportName, sportCode
        );
    }
}
