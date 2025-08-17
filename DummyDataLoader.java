package com.example.TeamKDT;

import com.example.TeamKDT.Entity.Post;
import com.example.TeamKDT.Entity.User;
import com.example.TeamKDT.Repository.PostRepository;
import com.example.TeamKDT.Repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DummyDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    public void run(String... args) throws Exception {
        // "testuser5"라는 사용자 없을 때만 추가
        if (userRepository.findByUsername("testuser5").isEmpty()) {
            User user = new User();
            user.setUsername("testuser5");
            user.setNickname("testnick5");
            user.setPassword("$2a$10$u9x4yYP2O1ANJUKF4Ru4yOaeYx.LC9Wj2Hy8oaAqzZXs1U1hKsqCy\t");
            User savedUser = userRepository.save(user);

            postRepository.save(new Post("첫 번째 게시글 제목", savedUser));
            postRepository.save(new Post("두 번째 게시글 제목", savedUser));
            postRepository.save(new Post("세 번째 게시글 제목", savedUser));
        }

        // "user1" 없을 때만 추가
        if (userRepository.findByUsername("user1").isEmpty()) {
            User user1 = new User();
            user1.setUsername("user1");
            user1.setNickname("usernick1");
            user1.setPassword("$2a$10$dummyPassword1");
            User savedUser1 = userRepository.save(user1);

            postRepository.save(new Post("게시글 제목1", savedUser1));
            postRepository.save(new Post("게시글 제목2", savedUser1));
        }

        // "user2" 없을 때만 추가
        if (userRepository.findByUsername("user2").isEmpty()) {
            User user2 = new User();
            user2.setUsername("user2");
            user2.setNickname("nick2");
            user2.setPassword("$2a$10$dummyPassword2");
            User savedUser2 = userRepository.save(user2);

            postRepository.save(new Post("1번째 게시글 제목", savedUser2));
            postRepository.save(new Post("2번째 게시글 제목", savedUser2));
        }
    }
}
