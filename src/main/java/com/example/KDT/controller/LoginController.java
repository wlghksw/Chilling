package com.example.KDT.controller;

import com.example.KDT.dto.UserDTO;
import com.example.KDT.entity.User;
import com.example.KDT.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/login")
    public String loginForm(Model model, String error, String logout) {
        if (error != null) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        
        if (logout != null) {
            model.addAttribute("successMessage", "로그아웃되었습니다.");
        }
        
        return "login";
    }
    
    @GetMapping("/register")
    public String showSignupForm(Model model) {
        // 모든 필드 초기화
        model.addAttribute("loginId", "");
        model.addAttribute("password", "");
        model.addAttribute("realName", "");
        model.addAttribute("nickname", "");
        model.addAttribute("phone", "");
        model.addAttribute("birthYear", "");
        model.addAttribute("gender", "male");
        model.addAttribute("profileImage", "");
        return "register";
    }

    @PostMapping("/register")
    public String signup(@ModelAttribute UserDTO dto, Model model) {
        try {
            userService.signup(dto);
            model.addAttribute("success", "회원가입이 완료되었습니다!");
            return "register_success";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            
            // 오류 발생 시 입력된 값들을 다시 모델에 추가
            model.addAttribute("loginId", dto.getLoginId() != null ? dto.getLoginId() : "");
            model.addAttribute("password", dto.getPassword() != null ? dto.getPassword() : "");
            model.addAttribute("realName", dto.getRealName() != null ? dto.getRealName() : "");
            model.addAttribute("nickname", dto.getNickname() != null ? dto.getNickname() : "");
            model.addAttribute("phone", dto.getPhone() != null ? dto.getPhone() : "");
            model.addAttribute("birthYear", dto.getBirthYear() != null ? dto.getBirthYear() : "");
            model.addAttribute("gender", dto.getGender() != null ? dto.getGender() : "male");
            model.addAttribute("profileImage", dto.getProfileImage() != null ? dto.getProfileImage() : "");
        }
        return "register";
    }

    @GetMapping("/check-id")
    public String checkId(@RequestParam String loginId, Model model) {
        boolean exists = userService.getUserByLoginId(loginId).isPresent();
        model.addAttribute("loginId", loginId);
        if (exists) {
            model.addAttribute("idError", "이미 사용 중인 아이디입니다.");
        } else {
            model.addAttribute("idSuccess", "사용 가능한 아이디입니다.");
        }
        
        // 다른 필드들도 초기화 (중복확인 후에도 폼이 유지되도록)
        model.addAttribute("password", "");
        model.addAttribute("realName", "");
        model.addAttribute("nickname", "");
        model.addAttribute("phone", "");
        model.addAttribute("birthYear", "");
        model.addAttribute("gender", "male");
        model.addAttribute("profileImage", "");
        
        return "register";
    }

    @GetMapping("/find-id")
    public String findIdForm(Model model) {
        System.out.println("=== find-id GET 요청 처리 ===");
        try {
            // 초기값 설정
            model.addAttribute("realName", "");
            model.addAttribute("phone", "");
            model.addAttribute("birthYear", "");
            System.out.println("모델 속성 설정 완료");
            return "find_id";
        } catch (Exception e) {
            System.out.println("find-id 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    @PostMapping("/find-id")
    public String findId(@RequestParam String realName,
                         @RequestParam String phone,
                         @RequestParam Integer birthYear,
                         Model model) {
        
        System.out.println("=== 아이디 찾기 요청 ===");
        System.out.println("실명: " + realName);
        System.out.println("전화번호: " + phone);
        System.out.println("출생년도: " + birthYear);

        String loginId = userService.findLoginId(realName, phone, birthYear);
        
        System.out.println("찾은 아이디: " + loginId);

        if (loginId != null) {
            model.addAttribute("loginId", loginId);
        } else {
            model.addAttribute("error", "일치하는 회원을 찾을 수 없습니다.");
        }

        model.addAttribute("realName", realName);
        model.addAttribute("phone", phone);
        model.addAttribute("birthYear", birthYear);

        return "find_id";
    }
    
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "로그아웃되었습니다.");
        return "redirect:/main";
    }
}
