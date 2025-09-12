package com.example.KDT.controller;

import com.example.KDT.entity.Post;
import com.example.KDT.entity.CommunityPost;
import com.example.KDT.entity.MyPost;
import com.example.KDT.entity.User;
import com.example.KDT.entity.MatchApplication;
import com.example.KDT.service.AdminService;
import com.example.KDT.service.MatchService;
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
    
    @Autowired
    private MatchService matchService;
    
    @GetMapping
    public String adminDashboard(Model model) {
        try {
            System.out.println("=== 관리자 대시보드 접근 ===");
            
            // 모든 유저 게시글 조회
            List<CommunityPost> communityPosts = adminService.getAllCommunityPosts();
            List<MyPost> myPosts = adminService.getAllMyPosts();
            List<User> users = adminService.getAllUsers();
            
            // 전체 게시글 수 계산 (커뮤니티 + 내소개)
            int totalPostsCount = (communityPosts != null ? communityPosts.size() : 0) + 
                                (myPosts != null ? myPosts.size() : 0);
            
            System.out.println("조회된 커뮤니티 게시글 수: " + (communityPosts != null ? communityPosts.size() : 0));
            System.out.println("조회된 내소개 게시글 수: " + (myPosts != null ? myPosts.size() : 0));
            System.out.println("조회된 사용자 수: " + (users != null ? users.size() : 0));
            System.out.println("전체 게시글 수: " + totalPostsCount);
            
            model.addAttribute("communityPosts", communityPosts != null ? communityPosts : new ArrayList<>());
            model.addAttribute("myPosts", myPosts != null ? myPosts : new ArrayList<>());
            model.addAttribute("users", users != null ? users : new ArrayList<>());
            model.addAttribute("totalPosts", totalPostsCount);
            model.addAttribute("totalUsers", users != null ? users.size() : 0);
            
            return "admin/dashboard";
        } catch (Exception e) {
            System.err.println("관리자 대시보드 에러: " + e.getMessage());
            e.printStackTrace();
            // 에러 발생 시 빈 데이터로 대시보드 표시
            model.addAttribute("communityPosts", new ArrayList<>());
            model.addAttribute("myPosts", new ArrayList<>());
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
            // 유저 게시글만 조회 (커뮤니티 + 내소개)
            List<CommunityPost> communityPosts = adminService.getAllCommunityPosts();
            List<MyPost> myPosts = adminService.getAllMyPosts();
            
            // 전체 게시글 수 계산
            int totalPostsCount = (communityPosts != null ? communityPosts.size() : 0) + 
                                (myPosts != null ? myPosts.size() : 0);
            
            model.addAttribute("communityPosts", communityPosts != null ? communityPosts : new ArrayList<>());
            model.addAttribute("myPosts", myPosts != null ? myPosts : new ArrayList<>());
            model.addAttribute("postsCount", totalPostsCount);
            model.addAttribute("keyword", "");
            model.addAttribute("username", "");
            
            System.out.println("=== 관리자 게시글 관리 페이지 ===");
            System.out.println("커뮤니티 게시글: " + (communityPosts != null ? communityPosts.size() : 0) + "개");
            System.out.println("내소개 게시글: " + (myPosts != null ? myPosts.size() : 0) + "개");
            System.out.println("전체 게시글: " + totalPostsCount + "개");
            
            return "admin/posts";
        } catch (Exception e) {
            System.err.println("게시글 관리 페이지 에러: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("communityPosts", new ArrayList<>());
            model.addAttribute("myPosts", new ArrayList<>());
            model.addAttribute("postsCount", 0);
            model.addAttribute("keyword", "");
            model.addAttribute("username", "");
            model.addAttribute("errorMessage", "게시글을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
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
    
    // 커뮤니티 게시글 삭제
    @PostMapping("/community-posts/{postId}/delete")
    @ResponseBody
    public String deleteCommunityPost(@PathVariable Integer postId) {
        try {
            adminService.deleteCommunityPost(postId);
            return "success";
        } catch (Exception e) {
            System.err.println("커뮤니티 게시글 삭제 에러: " + e.getMessage());
            e.printStackTrace();
            return "error";
        }
    }
    
    // 내소개 게시글 삭제
    @PostMapping("/my-posts/{postId}/delete")
    @ResponseBody
    public String deleteMyPost(@PathVariable Integer postId) {
        try {
            adminService.deleteMyPost(postId);
            return "success";
        } catch (Exception e) {
            System.err.println("내소개 게시글 삭제 에러: " + e.getMessage());
            e.printStackTrace();
            return "error";
        }
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
            System.out.println("=== 사용자 관리 페이지 접근 ===");
            
            List<User> users = adminService.getAllUsers();
            System.out.println("조회된 사용자 수: " + (users != null ? users.size() : 0));
            
            // 각 사용자의 게시글 수를 User 객체에 직접 설정
            for (User user : users) {
                int postCount = adminService.getUserPostCount(user.getId());
                user.setPostCount(postCount);
                
                // null 값 처리
                if (user.getIsAdmin() == null) {
                    user.setIsAdmin(false);
                }
                if (user.getIsActive() == null) {
                    user.setIsActive(true);
                }
                if (user.getEmail() == null) {
                    user.setEmail("이메일 없음");
                }
                
                System.out.println("사용자: " + user.getNickname() + 
                                 ", ID: " + user.getId() + 
                                 ", 관리자: " + user.getIsAdmin() + 
                                 ", 활성: " + user.getIsActive() + 
                                 ", 이메일: " + user.getEmail() +
                                 ", 게시글수: " + postCount);
            }
            
            model.addAttribute("users", users != null ? users : new ArrayList<>());
            model.addAttribute("usersCount", users != null ? users.size() : 0);
            return "admin/users";
        } catch (Exception e) {
            System.err.println("사용자 관리 페이지 에러: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("users", new ArrayList<>());
            model.addAttribute("usersCount", 0);
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
            Boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
            adminService.updateUserRole(id, isAdmin);
            redirectAttributes.addFlashAttribute("successMessage", "사용자 역할이 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "사용자 역할 변경 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/{id}/force-withdraw")
    public String forceWithdrawUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.forceWithdrawUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "사용자가 강제 탈퇴 처리되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "사용자 강제 탈퇴 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/{id}/restore")
    public String restoreUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.restoreUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "사용자 계정이 복구되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "사용자 계정 복구 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/applications")
    public String adminApplications(Model model) {
        try {
            // 모든 매치 신청 조회 (승인 대기 중인 것들)
            List<MatchApplication> pendingApplications = new ArrayList<>();
            List<MatchApplication> allApplications = matchService.getAllMatchApplications();
            
            for (MatchApplication app : allApplications) {
                if (app.getStatus() == MatchApplication.ApplicationStatus.PENDING) {
                    pendingApplications.add(app);
                }
            }
            
            model.addAttribute("applications", pendingApplications);
            model.addAttribute("applicationsCount", pendingApplications.size());
            return "admin/applications";
        } catch (Exception e) {
            model.addAttribute("applications", new ArrayList<>());
            model.addAttribute("applicationsCount", 0);
            model.addAttribute("errorMessage", "신청 목록을 불러오는 중 오류가 발생했습니다.");
            return "admin/applications";
        }
    }
    
    @PostMapping("/applications/{id}/approve")
    public String approveApplication(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            matchService.approveApplication(id);
            redirectAttributes.addFlashAttribute("successMessage", "매치 신청이 승인되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "신청 승인 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/applications";
    }
    
    @PostMapping("/applications/{id}/reject")
    public String rejectApplication(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            matchService.rejectApplication(id);
            redirectAttributes.addFlashAttribute("successMessage", "매치 신청이 거절되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "신청 거절 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/applications";
    }
}
