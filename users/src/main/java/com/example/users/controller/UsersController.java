package com.example.users.controller;

import com.example.users.dto.UsersDTO;
import com.example.users.entity.Users;
import com.example.users.repository.UsersRepository;
import com.example.users.service.UsersService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;
    private final UsersRepository usersRepository;

    @GetMapping("/signup")
    public String showSignupForm() {
        return "users/signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute UsersDTO dto, Model model) {
        try {
            usersService.signup(dto);
            model.addAttribute("success", "회원가입이 완료되었습니다!");
            return "users/signup_success";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "users/signup";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "users/login";
    }


    @PostMapping("/login")
    public String login(@RequestParam String login_id,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            Users user = usersService.login(login_id, password);

            session.setAttribute("LOGIN_USER_ID", user.getUser_id()); // PK
            session.setAttribute("LOGIN_ID", user.getLogin_id());     // 로그인 아이디

            model.addAttribute("nickname", user.getNickname());
            model.addAttribute("success", user.getNickname() + "님 환영합니다");
            return "users/main"; // -> 메인
        } catch (RuntimeException e) {
            model.addAttribute("error", "로그인 실패: " + e.getMessage());
            model.addAttribute("loginId", login_id);
            return "users/login";
        }
    }

    @GetMapping("/check-id")
    public String checkId(@RequestParam String login_id, Model model) {
        boolean exists = usersRepository.existsByLoginId(login_id);
        model.addAttribute("login_id", login_id);
        if (exists) {
            model.addAttribute("idError", "이미 사용 중인 아이디입니다.");
        } else {
            model.addAttribute("idSuccess", "사용 가능한 아이디입니다.");
        }

        model.addAttribute("loginIdValue", login_id);
        return "users/signup";
    }

    @GetMapping("/find_id")
    public String findIdForm() {

        return "users/find_id";
    }
    @PostMapping("/find_id")
    public String FindId(@RequestParam String realName,
                         @RequestParam String phone,
                         @RequestParam Integer birthYear,
                         Model model) {

        String loginId = usersService.findLoginId(realName, phone, birthYear);

        if (loginId != null) {
            model.addAttribute("loginId" , loginId);
        } else {
            model.addAttribute("error", "일치하는 회원을 찾을 수 없습니다.");
        }

        model.addAttribute("realName", realName);
        model.addAttribute("phone", phone);
        model.addAttribute("birthYear", birthYear);

        return "users/find_id";
    }

    @GetMapping("/posts/new")
    public String newPostForm(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("LOGIN_USER_ID");
        if (userId == null) return "redirect:/users/login"; // 미로그인 → 로그인 화면

        Users user = usersRepository.findByUserId(userId).orElseThrow();
        model.addAttribute("loginId", user.getLogin_id());
        model.addAttribute("nickname", user.getNickname());
        //model.addAttribute("form", new PostCreateForm());
        return "posts/new";
    }


}

