package com.example.KDT.controller;

import com.example.KDT.entity.Post;
import com.example.KDT.entity.User;
import com.example.KDT.repository.PostRepository;
import com.example.KDT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TestController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @GetMapping("/test/data")
    public Map<String, Object> testData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            long userCount = userRepository.count();
            long postCount = postRepository.count();
            
            List<User> users = userRepository.findAll();
            List<Post> posts = postRepository.findAll();
            
            result.put("userCount", userCount);
            result.put("postCount", postCount);
            result.put("users", users);
            result.put("posts", posts);
            result.put("success", true);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
}
