package com.example.KDT.service;

import com.example.KDT.entity.Post;
import com.example.KDT.entity.User;
import com.example.KDT.entity.UserRole;
import com.example.KDT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdminService {
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }
    
    public Optional<Post> getPostById(Long id) {
        return postService.getPostById(id);
    }
    
    public void deletePost(Long postId) {
        if (postService.existsById(postId)) {
            postService.deletePost(postId);
        } else {
            throw new RuntimeException("게시글을 찾을 수 없습니다.");
        }
    }
    
    public List<Post> searchPostsByKeyword(String keyword) {
        return postService.searchPosts(keyword);
    }
    
    public List<Post> searchPostsByUsername(String username) {
        return postService.getPostsByUsername(username);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public void deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
    }
    
    public User updateUserRole(Long userId, UserRole role) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(role);
            return userRepository.save(user);
        } else {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
    }
}
