package com.example.TeamKDT.Service;

import com.example.TeamKDT.DTO.PostDTO;
import com.example.TeamKDT.Entity.Post;
import com.example.TeamKDT.Entity.User;
import com.example.TeamKDT.Repository.PostRepository;
import com.example.TeamKDT.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public User getUserByUserId(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public List<PostDTO> getPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByUser_UserId(userId);
        if (posts == null || posts.isEmpty()) {
            return Collections.emptyList();
        }
        return posts.stream()
                .map(post -> new PostDTO(
                        post.getPostId(),
                        post.getTitle(),
                        post.getUserId() != null ? post.getUserId().getUserId() : null
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateUserNickname(Long userId, String nickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setNickname(nickname);
    }

    @Transactional
    public void updatePostTitle(Long postId, String title) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        post.setTitle(title);
    }
}

