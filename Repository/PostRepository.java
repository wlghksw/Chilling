package com.example.TeamKDT.Repository;

import com.example.TeamKDT.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser_UserId(Long userId);
}