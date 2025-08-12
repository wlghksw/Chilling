package com.example.KDT.controller;

import com.example.KDT.entity.Post;
import com.example.KDT.entity.User;
import com.example.KDT.repository.PostRepository;
import com.example.KDT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/count")
    public Map<String, Object> getCounts() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            long userCount = userRepository.count();
            long postCount = postRepository.count();
            
            result.put("userCount", userCount);
            result.put("postCount", postCount);
            result.put("success", true);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    @PostMapping("/create-dummy")
    public Map<String, Object> createDummyData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 간단한 더미 게시글 생성
            Post post = new Post();
            post.setTitle("테스트 게시글");
            post.setContent("이것은 테스트 게시글입니다.");
            post.setViewCount(0);
            post.setLikeCount(0);
            post.setIsNotice(false);
            post.setIsActive(true);
            post.setCreatedAt(LocalDateTime.now());
            post.setUpdatedAt(LocalDateTime.now());
            
            // 첫 번째 사용자를 작성자로 설정
            List<User> users = userRepository.findAll();
            if (!users.isEmpty()) {
                post.setAuthor(users.get(0));
                
                // category와 sport는 null로 설정 (필수가 아닌 경우)
                post.setCategory(null);
                post.setSport(null);
                
                postRepository.save(post);
                result.put("success", true);
                result.put("message", "더미 데이터 생성 완료");
            } else {
                result.put("success", false);
                result.put("message", "사용자가 없습니다");
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    @PostMapping("/create-simple-dummy")
    public Map<String, Object> createSimpleDummyData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 간단한 사용자 생성
            User user = new User();
            user.setLoginId("testuser");
            user.setNickname("테스트유저");
            user.setEmail("test@example.com");
            user.setPassword("1234");
            user.setRealName("테스트");
            user.setPhone("010-0000-0000");
            user.setBirthYear(1990);
            user.setGender("male");
            user.setIsActive(true);
            user.setIsAdmin(false);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            
            userRepository.save(user);
            
            // 간단한 게시글 생성
            Post post = new Post();
            post.setTitle("첫 번째 게시글");
            post.setContent("안녕하세요! 이것은 첫 번째 게시글입니다.");
            post.setAuthor(user);
            post.setViewCount(0);
            post.setLikeCount(0);
            post.setIsNotice(false);
            post.setIsActive(true);
            post.setCreatedAt(LocalDateTime.now());
            post.setUpdatedAt(LocalDateTime.now());
            
            postRepository.save(post);
            
            result.put("success", true);
            result.put("message", "간단한 더미 데이터 생성 완료");
            result.put("userId", user.getId());
            result.put("postId", post.getId());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/posts")
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @GetMapping("/simple")
    public String simpleTest() {
        return "Test controller is working!";
    }
}
