package com.example.KDT.project.repository;


import com.example.KDT.project.entity.Post;
import com.example.KDT.project.entity.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {


    // 특정 종목(sportId)에 대한 모든 게시물 조회
    // 'findBy' 다음에 'Sport' 엔티티의 필드명 'sportId'를 사용
    List<Post> findBySport_SportId(Integer sportId);

    // 공지사항(isNotice = true) 조회
    List<Post> findByIsNoticeTrue();

    List<Post> findByIsNotice(Boolean isNotice);

    List<Post> findByCategory(PostCategory category);


    List<Post> findTop5ByOrderByViewCountDesc();


}

