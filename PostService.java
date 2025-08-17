package com.example.KDT.project.service;

import com.example.KDT.project.entity.Post;
import com.example.KDT.project.entity.PostCategory;
import com.example.KDT.project.entity.User;
import com.example.KDT.project.repository.PostCategoryRepository;
import com.example.KDT.project.repository.PostRepository;
import com.example.KDT.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostCategoryRepository postCategoryRepository;

    @Autowired
    private UserRepository userRepository;


    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }


    public List<Post> getPostsByCategoryType(PostCategory.CategoryType categoryType) {
        PostCategory category = postCategoryRepository.findByCategoryType(categoryType).orElse(null);
        if (category != null) {
            return postRepository.findByCategory(category);
        }
        return List.of(); // 빈 리스트 반환
    }


    public Post getPostById(Integer postId) {
        return postRepository.findById(postId).orElse(null);
    }


    public void savePost(String categoryName, String title, String content, Integer userId) {
        PostCategory category = postCategoryRepository.findByCategoryName(categoryName).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (category != null && user != null) {
            Post post = Post.builder()
                    .title(title)
                    .content(content)
                    .category(category)
                    .user(user)
                    .build();
            postRepository.save(post);
        }
    }


    public void updatePost(Integer postId, String title, String content) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post != null) {
            post.setTitle(title);
            post.setContent(content);
            postRepository.save(post);
        }
    }


    public void deletePost(Integer postId) {
        postRepository.deleteById(postId);
    }


    public List<Post> getTop5PostsByViewCount() {
        return postRepository.findTop5ByOrderByViewCountDesc();
    }
}