package com.example.users.repository;

import com.example.users.entity.Posts;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PostsRepository extends CrudRepository<Posts, Integer> {


        @Query("SELECT p FROM Posts p WHERE p.sport_id = :sportId AND p.category_id = :categoryId")
        List<Posts> findBySportIdAndCategoryId(@Param("sportId") Integer sportId,
                                               @Param("categoryId") Integer categoryId);

        @Query("""
           SELECT p FROM Posts p
           WHERE p.sport_id = :sportId
             AND p.category_id = :categoryId
             AND LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%'))
           ORDER BY p.post_id DESC
           """)
        List<Posts> searchByTitle(@Param("sportId") Integer sportId,
                                  @Param("categoryId") Integer categoryId,
                                  @Param("q") String q);




        //조회수 +1
        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query("UPDATE Posts p SET p.view_count = p.view_count + 1 WHERE p.post_id = :postId")
        int increaseViewCount(@Param("postId") Integer postId);


        // 최신순 (작성일 내림차순)
        @Query("""
        SELECT p FROM Posts p
        WHERE p.sport_id = :sportId
          AND p.category_id = :categoryId
        ORDER BY p.created_at DESC
        """)
        List<Posts> findBySportIdAndCategoryIdOrderByCreatedAtDesc(@Param("sportId") Integer sportId,
                                                                   @Param("categoryId") Integer categoryId);



        // 조회수순 (많은 → 적은)
        @Query("""
        SELECT p FROM Posts p
        WHERE p.sport_id = :sportId
          AND p.category_id = :categoryId
        ORDER BY p.view_count DESC, p.post_id DESC
        """)
        List<Posts> findBySportIdAndCategoryIdOrderByViewCountDesc(@Param("sportId") Integer sportId,
                                                                   @Param("categoryId") Integer categoryId);

        // 좋아요순 (많은 → 적은)
        @Query("""
        SELECT p FROM Posts p
        WHERE p.sport_id = :sportId
        AND p.category_id = :categoryId
        ORDER BY p.like_count DESC, p.post_id DESC
        """)
        List<Posts> findBySportIdAndCategoryIdOrderByLikeCountDesc(@Param("sportId") Integer sportId,
                                                                   @Param("categoryId") Integer categoryId);

        @Query("""
        SELECT p FROM Posts p
        WHERE p.user_id = :userId
        AND p.sport_id = :sportId
        AND p.category_id = :categoryId
        AND p.title = :title
        ORDER BY p.created_at DESC, p.post_id DESC
        """)
        List<Posts> findLatestByUserAndSportAndCategoryAndTitle(
                @Param("userId") Integer userId,
                @Param("sportId") Integer sportId,
                @Param("categoryId") Integer categoryId,
                @Param("title") String title
        );
}


