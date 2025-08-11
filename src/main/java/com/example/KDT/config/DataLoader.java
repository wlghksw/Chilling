package com.example.KDT.config;

import com.example.KDT.entity.Post;
import com.example.KDT.entity.User;
import com.example.KDT.entity.UserRole;
import com.example.KDT.repository.PostRepository;
import com.example.KDT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Spring Security가 완전히 초기화된 후에 실행되도록 지연
        try {
            Thread.sleep(2000);
            System.out.println("=== 테스트 데이터 생성 시작 ===");
            
            // 기존 데이터가 없을 때만 테스트 데이터 생성
            long userCount = userRepository.count();
            long postCount = postRepository.count();
            
            System.out.println("현재 사용자 수: " + userCount);
            System.out.println("현재 게시글 수: " + postCount);
            
            if (userCount == 0) {
                createTestData();
                System.out.println("=== 테스트 데이터 생성 완료 ===");
                System.out.println("생성된 사용자 수: " + userRepository.count());
                System.out.println("생성된 게시글 수: " + postRepository.count());
            } else {
                System.out.println("이미 데이터가 존재합니다.");
            }
        } catch (Exception e) {
            // 에러가 발생해도 애플리케이션은 계속 실행
            System.err.println("테스트 데이터 생성 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createTestData() {
        // 관리자 사용자 생성
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin123");
        admin.setEmail("admin@example.com");
        admin.setRole(UserRole.ADMIN);
        admin.setCreatedAt(LocalDateTime.now());
        userRepository.save(admin);
        
        // 일반 사용자들 생성
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("user123");
        user1.setEmail("user1@example.com");
        user1.setRole(UserRole.USER);
        user1.setCreatedAt(LocalDateTime.now());
        userRepository.save(user1);
        
        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("user123");
        user2.setEmail("user2@example.com");
        user2.setRole(UserRole.USER);
        user2.setCreatedAt(LocalDateTime.now());
        userRepository.save(user2);
        
        User user3 = new User();
        user3.setUsername("user3");
        user3.setPassword("user123");
        user3.setEmail("user3@example.com");
        user3.setRole(UserRole.USER);
        user3.setCreatedAt(LocalDateTime.now());
        userRepository.save(user3);
        
        // 테스트 게시글들 생성
        createPost(user1, "첫 번째 게시글입니다", "안녕하세요! 이것은 첫 번째 게시글의 내용입니다. 많은 관심 부탁드립니다.");
        createPost(user1, "운동에 대한 생각", "규칙적인 운동의 중요성에 대해 이야기해보고 싶습니다.");
        createPost(user2, "건강한 식단", "건강한 식단 관리 방법을 공유합니다.");
        createPost(user2, "주말 등산 후기", "지난 주말에 다녀온 등산 코스 후기를 작성합니다.");
        createPost(user3, "요가 수업 추천", "초보자를 위한 요가 수업을 추천합니다.");
        createPost(user3, "헬스장 이용 팁", "헬스장을 처음 이용하는 분들을 위한 팁을 공유합니다.");
        createPost(admin, "관리자 공지사항", "이것은 관리자가 작성한 공지사항입니다.");
    }
    
    private void createPost(User author, String title, String content) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
    }
}
