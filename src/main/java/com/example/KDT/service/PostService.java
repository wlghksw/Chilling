package com.example.KDT.service;

import com.example.KDT.entity.Post;
import com.example.KDT.entity.User;
import com.example.KDT.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;

    /* 페이징 조회 (최근순, author fetch join) */
    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findAllWithAuthor(pageable);
    }

    /* 전체 조회 (최근순, author fetch join) */
    public List<Post> getAllPosts() {
        return postRepository.findAllWithAuthor();
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public List<Post> getPostsByAuthor(User author) {
        return postRepository.findByAuthorOrderByCreatedAtDesc(author);
    }

    public List<Post> searchPosts(String keyword) {
        return postRepository.searchByKeyword(keyword);
    }

    public List<Post> getPostsByUsername(String username) {
        // username 파라미터는 닉네임을 의미하도록 컨트롤러/뷰에서 라벨만 맞춰주면 됩니다.
        return postRepository.findByAuthorNicknameContaining(username);
    }
    
    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByAuthorId(userId);
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public Post updatePost(Post post) {
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return postRepository.existsById(id);
    }
}
