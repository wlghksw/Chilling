package com.example.KDT.project.repository;

import com.example.KDT.project.entity.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory, Integer> {


    Optional<PostCategory> findByCategoryName(String categoryName);


    Optional<PostCategory> findByCategoryType(PostCategory.CategoryType categoryType);
}
