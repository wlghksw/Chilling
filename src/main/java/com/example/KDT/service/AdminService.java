package com.example.KDT.service;

import com.example.KDT.entity.Post;
import com.example.KDT.entity.CommunityPost;
import com.example.KDT.entity.MyPost;
import com.example.KDT.entity.User;
import com.example.KDT.repository.UserRepository;
import com.example.KDT.repository.CommunityPostRepository;
import com.example.KDT.repository.MyPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
@Transactional
public class AdminService {
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CommunityPostRepository communityPostRepository;
    
    @Autowired
    private MyPostRepository myPostRepository;
    
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }
    
    public List<CommunityPost> getAllCommunityPosts() {
        Iterable<CommunityPost> iterable = communityPostRepository.findAll();
        List<CommunityPost> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
    
    public List<MyPost> getAllMyPosts() {
        Iterable<MyPost> iterable = myPostRepository.findAll();
        List<MyPost> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
    
    public void deleteCommunityPost(Integer postId) {
        if (communityPostRepository.existsById(postId)) {
            communityPostRepository.deleteById(postId);
        } else {
            throw new RuntimeException("커뮤니티 게시글을 찾을 수 없습니다.");
        }
    }
    
    public void deleteMyPost(Integer postId) {
        if (myPostRepository.existsById(postId)) {
            myPostRepository.deleteById(postId);
        } else {
            throw new RuntimeException("내소개 게시글을 찾을 수 없습니다.");
        }
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
    
    public List<User> getUsersByRole(Boolean isAdmin) {
        return userRepository.findByIsAdmin(isAdmin);
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
    
    public User updateUserRole(Long userId, Boolean isAdmin) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsAdmin(isAdmin);
            return userRepository.save(user);
        } else {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
    }
    
    public User forceWithdrawUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsActive(false);
            user.setUpdatedAt(java.time.LocalDateTime.now());
            return userRepository.save(user);
        } else {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
    }
    
    public User restoreUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsActive(true);
            user.setUpdatedAt(java.time.LocalDateTime.now());
            return userRepository.save(user);
        } else {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
    }
    
    public int getUserPostCount(Long userId) {
        return postService.getPostsByUserId(userId).size();
    }
}
