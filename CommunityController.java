package com.example.KDT.project.controller;

import com.example.KDT.project.entity.Post;
import com.example.KDT.project.entity.PostCategory;
import com.example.KDT.project.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/community")
public class CommunityController {

    @Autowired
    private PostService postService;

    // 커뮤니티 페이지로 접속하면 기본적으로 공지사항을 보여주도록 함
    @GetMapping({"", "/{category}"})
    public String showCommunity(@PathVariable(required = false) String category, Model model) {

        // category가 null이거나 빈 문자열이면 "notice"로 기본값 설정
        if (category == null || category.isEmpty()) {
            category = "notice";
        }

        PostCategory.CategoryType categoryType;
        try {
            categoryType = PostCategory.CategoryType.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 올바르지 않은 카테고리가 들어오면 기본값 "notice"로 설정
            categoryType = PostCategory.CategoryType.NOTICE;
        }
        String selectedCategoryName = (categoryType == PostCategory.CategoryType.NOTICE) ? "공지사항" : "자유게시판";


        List<Post> posts = postService.getPostsByCategoryType(categoryType);

        model.addAttribute("posts", posts);
        model.addAttribute("selectedCategoryName", selectedCategoryName);
        model.addAttribute("category", category);

        // 무스타치용
        model.addAttribute("selectedCategory_Notice", category.equals("notice"));
        model.addAttribute("selectedCategory_Community", category.equals("community"));

        return "/communityPage";
    }

    // 새 게시글 작성 페이지를 보여줌
    @GetMapping("/{category}/new")
    public String newPostForm(@PathVariable String category, Model model) {
        model.addAttribute("category", category);
        return "post-new";
    }

    // 게시글을 저장
    @PostMapping("/{category}")
    public String savePost(@PathVariable String category,
                           @RequestParam String title,
                           @RequestParam String content,
                           RedirectAttributes redirectAttributes) {
        try {
            Integer userId = 1; // 로그인 세션에서 실제 유저 ID를 가져와야 함
            postService.savePost(category, title, content, userId);
            // 게시글 저장 후 해당 카테고리 목록으로 리디렉션
            return "redirect:/community/" + category;


        } catch (Exception e) {
            // 오류 발생 시 오류 메시지를 리다이렉트 페이지로 전달
            redirectAttributes.addFlashAttribute("errorMessage", "게시글 작성 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/community/" + category + "/new";
        }
    }

    // 게시글 상세 페이지를 보여줌
    @GetMapping("/{category}/{postId}")
    public String showPostDetail(@PathVariable String category,
                                 @PathVariable Integer postId,
                                 Model model) {
        Post post = postService.getPostById(postId);
        model.addAttribute("post", post);
        model.addAttribute("category", category);
        return "post-detail";
    }

    // 게시글 수정 페이지
    @GetMapping("/{category}/{postId}/edit")
    public String editPostForm(@PathVariable String category,
                               @PathVariable Integer postId,
                               Model model) {
        Post post = postService.getPostById(postId);
        model.addAttribute("post", post);
        model.addAttribute("category", category);
        return "post-edit";
    }

    // 게시글 업데이트
    @PutMapping("/{category}/{postId}")
    public String updatePost(@PathVariable String category,
                             @PathVariable Integer postId,
                             @RequestParam String title,
                             @RequestParam String content) {
        postService.updatePost(postId, title, content);
        return "redirect:/community/" + category + "/" + postId;
    }

    // 게시글을 삭제
    @DeleteMapping("/{category}/{postId}")
    public String deletePost(@PathVariable String category,
                             @PathVariable Integer postId) {
        postService.deletePost(postId);
        return "redirect:/community/" + category;
    }
}
