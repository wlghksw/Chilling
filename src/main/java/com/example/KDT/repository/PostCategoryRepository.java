package com.example.KDT.repository;

import com.example.KDT.entity.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory, Long> {
    
    List<PostCategory> findByCategoryType(String categoryType);
    
    List<PostCategory> findBySportSportId(Long sportId);
    
    List<PostCategory> findByCategoryTypeAndSportIsNull(String categoryType);
    
    // 커뮤니티 게시글용 메서드들
    Optional<PostCategory> findByCategoryTypeIgnoreCase(String categoryType);
    
    Optional<PostCategory> findByCategoryName(String categoryName);
}




