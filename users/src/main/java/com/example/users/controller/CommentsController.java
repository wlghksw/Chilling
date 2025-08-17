package com.example.users.controller;

import com.example.users.repository.CommentsRepository;
import com.example.users.repository.PostsRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.users.entity.Comments;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class CommentsController {

    private final CommentsRepository commentsRepository;
    private final PostsRepository postsRepository;

    //댓글 작성
    @PostMapping("/{id}/comments")
    @Transactional
    public String add(@PathVariable Integer id,
                      @RequestParam String content,
                      @RequestParam(name = "parentCommentId", required = false) Integer parentCommentId,
                      HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        Long loginUserId = (session == null) ? null : (Long) session.getAttribute("LOGIN_USER_ID");
        if (loginUserId == null) return "redirect:/users/login";

        if (content == null || content.isBlank()) {
            return "redirect:/post/" + id + "?error=empty_comment#comments";
        }
        // 글 존재 체크
        postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 없습니다. id=" + id));

        if (parentCommentId != null) {
            var parent = commentsRepository.findById(parentCommentId)
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 없습니다."));
            if (!Objects.equals(parent.getPost_id(), id)) {
                throw new IllegalArgumentException("부모 댓글의 글과 일치하지 않습니다.");
            }
            if (Boolean.FALSE.equals(parent.getIs_active())) {
                throw new IllegalArgumentException("삭제된 댓글에는 대댓글을 달 수 없습니다.");
            }
        }

        Comments c = new Comments();
        c.setPost_id(id);
        c.setUser_id(loginUserId.intValue());
        c.setParent_comment_id(parentCommentId);
        c.setContent(content.trim());
        commentsRepository.save(c);

        return "redirect:/post/" + id + "#comments";
    }

    // 댓글 삭제
    @PostMapping("/comments/{commentId}/delete")
    @Transactional
    public String delete(@PathVariable Integer commentId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long loginUserId = (session == null) ? null : (Long) session.getAttribute("LOGIN_USER_ID");
        if (loginUserId == null) return "redirect:/users/login";

        var c = commentsRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("없는 댓글입니다."));

        if (!loginUserId.equals(c.getUser_id().longValue())) {
            return "redirect:/post/" + c.getPost_id() + "?error=forbidden#comments";
        }

        commentsRepository.softDeleteById(commentId);

        return "redirect:/post/" + c.getPost_id() + "#comments";
    }

}
