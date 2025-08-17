package com.example.users.repository;

import com.example.users.entity.Comments;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentsRepository extends CrudRepository<Comments,Integer> {


    @Query("""
        SELECT c FROM Comments c
        WHERE c.post_id = :postId
          AND c.is_active = TRUE
        ORDER BY c.created_at ASC, c.comment_id ASC
    """)
    List<Comments> findByPostIdOrderByCreatedAtAsc(@Param("postId") Integer postId);


    @Query("""
        SELECT c FROM Comments c
        WHERE c.post_id = :postId
          AND c.parent_comment_id IS NULL
          AND c.is_active = TRUE
        ORDER BY c.created_at ASC, c.comment_id ASC
    """)
    List<Comments> findTopLevelByPostId(@Param("postId") Integer postId);


    @Query("""
        SELECT c FROM Comments c
        WHERE c.parent_comment_id = :parentId
          AND c.is_active = TRUE
        ORDER BY c.created_at ASC, c.comment_id ASC
    """)
    List<Comments> findReplies(@Param("parentId") Integer parentId);


    @Query("SELECT COUNT(c) FROM Comments c WHERE c.post_id = :postId AND c.is_active = TRUE")
    long countActiveByPostId(@Param("postId") Integer postId);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Comments c
        SET c.is_active = FALSE, c.updated_at = current_timestamp
        WHERE c.comment_id = :commentId
    """)
    int softDeleteById(@Param("commentId") Integer commentId);

    // (선택) 내용 수정
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Comments c
        SET c.content = :content, c.updated_at = current_timestamp
        WHERE c.comment_id = :commentId AND c.is_active = TRUE
    """)
    int updateContent(@Param("commentId") Integer commentId, @Param("content") String content);

    // (선택) 글 삭제 시 댓글 일괄 삭제
    @Modifying
    @Query("DELETE FROM Comments c WHERE c.post_id = :postId")
    int deleteByPostIdHard(@Param("postId") Integer postId);


    @Query("""
   SELECT c FROM Comments c
   WHERE c.post_id = :postId AND (c.is_active = TRUE OR c.is_active IS NULL)
   ORDER BY c.created_at ASC
""")
    List<Comments> findAllActiveByPostId(@Param("postId") Integer postId);

}
