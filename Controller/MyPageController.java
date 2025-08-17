package com.example.TeamKDT.Controller;

import com.example.TeamKDT.DTO.PostDTO;
import com.example.TeamKDT.DTO.UserPostForm;
import com.example.TeamKDT.Entity.User;
import com.example.TeamKDT.Repository.UserRepository;
import com.example.TeamKDT.Service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;
    private final UserRepository userRepository;

    // 조회
    @GetMapping("/mypage/{userId}/read")
    public String myPage(@PathVariable Long userId, Model model) {
        log.info("userId: {}", userId);

        User user = myPageService.getUserByUserId(userId);
        List<PostDTO> posts = myPageService.getPostsByUserId(userId);

        if (user != null) {
            model.addAttribute("username", user.getNickname());
            model.addAttribute("userId",user.getUserId());
            model.addAttribute("posts", posts);
        } else {
            model.addAttribute("username", "알 수 없는 사용자");
            model.addAttribute("userId",0);
            model.addAttribute("posts", List.of());
        }

        return "mypage/read";
    }

    // 수정
    @GetMapping("/mypage/{userId}/edit")
    public String edit(@PathVariable Long userId, Model model) {
        User userEntity = userRepository.findById(userId).orElse(null);

        if (userEntity == null) {
            log.error("해당 유저를 찾을 수 없습니다: {}", userId);
            return "redirect:/error";
        }

        List<PostDTO> posts = myPageService.getPostsByUserId(userId);
        for (int i = 0; i < posts.size(); i++) {
            posts.get(i).setIndex(i);
        }
        model.addAttribute("user", userEntity);
        model.addAttribute("posts", posts);

        return "mypage/edit";
    }


    @PostMapping("/mypage/update")
    public String update(@ModelAttribute UserPostForm form) {
        log.info("닉네임 및 게시글 수정 요청: {}", form);

        myPageService.updateUserNickname(form.getUserId(), form.getNickname());
        for (PostDTO post : form.getPosts()) {
            myPageService.updatePostTitle(post.getPostId(), post.getTitle());
        }
        return "redirect:/mypage/" + form.getUserId() + "/read";
    }

    // 삭제(더미데이터는 초기화 시 리셋됨.)
    @GetMapping("/mypage/{id}/delete")
    public String delete(@PathVariable("id") Long userId, RedirectAttributes msg) {
        log.info("회원 삭제 요청: {}", userId);

        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            userRepository.delete(user);
            msg.addFlashAttribute("message", "회원이 삭제되었습니다.");
        }

        return "redirect:/users";
    }
}
