package com.example.users.repository;

import com.example.users.entity.PostMedia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

//보류!!
public interface PostMediaRepository extends CrudRepository<PostMedia, Integer> {

    @Query("SELECT COALESCE(MAX(pm.mediaOrder), 0) FROM PostMedia pm WHERE pm.postId = :postId")
    Integer findMaxOrderByPostId(@Param("postId") Integer postId);

    List<PostMedia> findAllByPostIdOrderByMediaOrderAsc(Integer postId);
}