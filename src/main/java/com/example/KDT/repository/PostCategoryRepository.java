package com.example.KDT.repository;

import com.example.KDT.entity.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory, Long> {
    
    List<PostCategory> findByCategoryType(String categoryType);
    
    List<PostCategory> findBySportSportId(Long sportId);
    
    List<PostCategory> findByCategoryTypeAndSportIsNull(String categoryType);
}




