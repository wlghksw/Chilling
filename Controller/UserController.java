package com.example.TeamKDT.Controller;

import com.example.TeamKDT.Entity.User;
import com.example.TeamKDT.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/{userId}/edit")
    public String edit(@PathVariable Long UserId, Model model) {
        User userEntity = userRepository.findById(UserId).orElse(null);
        model.addAttribute("user", userEntity);

        return "mypage/edit";
    }

}
