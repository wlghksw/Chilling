package com.example.KDT.controller;

import com.example.KDT.entity.Post;
import com.example.KDT.entity.User;
import com.example.KDT.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @GetMapping
    public String adminDashboard(Model model) {
        try {
            System.out.println("=== 관리자 대시보드 접근 ===");
            
            List<Post> posts = adminService.getAllPosts();
            List<User> users = adminService.getAllUsers();
            
            System.out.println("조회된 게시글 수: " + (posts != null ? posts.size() : 0));
            System.out.println("조회된 사용자 수: " + (users != null ? users.size() : 0));
            
            if (posts != null && !posts.isEmpty()) {
                System.out.println("첫 번째 게시글: " + posts.get(0).getTitle());
            }
            
            model.addAttribute("posts", posts != null ? posts : new ArrayList<>());
            model.addAttribute("users", users != null ? users : new ArrayList<>());
            model.addAttribute("totalPosts", posts != null ? posts.size() : 0);
            model.addAttribute("totalUsers", users != null ? users.size() : 0);
            
            return "admin/dashboard";
        } catch (Exception e) {
            System.err.println("관리자 대시보드 에러: " + e.getMessage());
            e.printStackTrace();
            // 에러 발생 시 빈 데이터로 대시보드 표시
            model.addAttribute("posts", new ArrayList<>());
            model.addAttribute("users", new ArrayList<>());
            model.addAttribute("totalPosts", 0);
            model.addAttribute("totalUsers", 0);
            model.addAttribute("errorMessage", "데이터를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
            return "admin/dashboard";
        }
    }

    @GetMapping("/posts")
    public String adminPosts(Model model) {
        try {
            List<Post> posts = adminService.getAllPosts();
            model.addAttribute("posts", posts != null ? posts : new ArrayList<>());
            model.addAttribute("postsCount", posts != null ? posts.size() : 0);
            model.addAttribute("keyword", "");   // ← 기본값
            model.addAttribute("username", "");  // ← 기본값
            return "admin/posts";
        } catch (Exception e) {
            model.addAttribute("posts", new ArrayList<>());
            model.addAttribute("postsCount", 0);
            model.addAttribute("keyword", "");
            model.addAttribute("username", "");
            model.addAttribute("errorMessage", "게시글을 불러오는 중 오류가 발생했습니다.");
            return "admin/posts";
        }
    }


    @GetMapping("/posts/search")
    public String searchPosts(@RequestParam String keyword, Model model) {
        List<Post> posts = adminService.searchPostsByKeyword(keyword);
        model.addAttribute("posts", posts);
        model.addAttribute("postsCount", posts != null ? posts.size() : 0);
        model.addAttribute("keyword", keyword);
        model.addAttribute("username", ""); // 다른 입력은 비워주기
        return "admin/posts";
    }

    @GetMapping("/posts/search/username")
    public String searchPostsByUsername(@RequestParam String username, Model model) {
        List<Post> posts = adminService.searchPostsByUsername(username);
        model.addAttribute("posts", posts);
        model.addAttribute("postsCount", posts != null ? posts.size() : 0);
        model.addAttribute("username", username);
        model.addAttribute("keyword", "");
        return "admin/posts";
    }


    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deletePost(id);
            redirectAttributes.addFlashAttribute("successMessage", "게시글이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "게시글 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/posts";
    }
    
    @GetMapping("/users")
    public String adminUsers(Model model) {
        try {
            List<User> users = adminService.getAllUsers();
            model.addAttribute("users", users != null ? users : new ArrayList<>());
            return "admin/users";
        } catch (Exception e) {
            model.addAttribute("users", new ArrayList<>());
            model.addAttribute("errorMessage", "사용자를 불러오는 중 오류가 발생했습니다.");
            return "admin/users";
        }
    }
    
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "사용자가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "사용자 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/{id}/role")
    public String updateUserRole(@PathVariable Long id, @RequestParam String role, RedirectAttributes redirectAttributes) {
        try {
            adminService.updateUserRole(id, com.example.KDT.entity.UserRole.valueOf(role.toUpperCase()));
            redirectAttributes.addFlashAttribute("successMessage", "사용자 역할이 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "사용자 역할 변경 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
