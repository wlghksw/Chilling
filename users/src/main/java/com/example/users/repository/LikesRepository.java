package com.example.users.repository;

import com.example.users.entity.Likes;
import org.springframework.data.repository.CrudRepository;

public interface LikesRepository extends CrudRepository<Likes, Integer> {

    boolean existsByPostIdAndUserId(Integer postId, Integer userId);
    void deleteByPostIdAndUserId(Integer postId, Integer userId);
    long countByPostId(Integer postId);
}
