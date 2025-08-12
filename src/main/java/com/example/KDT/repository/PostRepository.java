package com.example.KDT.repository;

import com.example.KDT.entity.Post;
import com.example.KDT.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /* 최근순 페이징 + 작성자 즉시로딩 */
    @Query(
            value = "SELECT p FROM Post p JOIN FETCH p.author a ORDER BY p.createdAt DESC",
            countQuery = "SELECT COUNT(p) FROM Post p"
    )
    Page<Post> findAllWithAuthor(Pageable pageable);

    /* 최근순 전체 + 작성자 즉시로딩 */
    @Query("SELECT p FROM Post p JOIN FETCH p.author a ORDER BY p.createdAt DESC")
    List<Post> findAllWithAuthor();

    /* 특정 작성자 글 (최근순) */
    List<Post> findByAuthorOrderByCreatedAtDesc(User author);

    /* 제목/내용 키워드 검색 (대소문자 무시) + 작성자 즉시로딩 */
    @Query("""
        SELECT p FROM Post p JOIN FETCH p.author a
        WHERE LOWER(p.title)   LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(p.content) LIKE LOWER(CONCAT('%', :q, '%'))
        ORDER BY p.createdAt DESC
        """)
    List<Post> searchByKeyword(@Param("q") String keyword);

    /* 작성자 닉네임으로 검색 (대소문자 무시) + 작성자 즉시로딩 */
    @Query("""
        SELECT p FROM Post p JOIN FETCH p.author a
        WHERE LOWER(a.nickname) LIKE LOWER(CONCAT('%', :nickname, '%'))
        ORDER BY p.createdAt DESC
        """)
    List<Post> findByAuthorNicknameContaining(@Param("nickname") String nickname);
    
    /* 사용자 ID로 게시글 조회 */
    List<Post> findByAuthorId(Long authorId);
}
