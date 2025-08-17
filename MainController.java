package com.example.KDT.project.controller;

import com.example.KDT.project.entity.Board;
import com.example.KDT.project.entity.Post;
import com.example.KDT.project.repository.BoardRepository;
import com.example.KDT.project.service.BoardService;
import com.example.KDT.project.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;


import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Slf4j
@Controller
public class MainController {


    @Autowired
    private PostService postService;


    @GetMapping("/main")
    public String showMainPage(Model model) {
        // 조회수 기준으로 가장 인기 있는 게시글 5개 가져오기
        List<Post> topPosts = postService.getTop5PostsByViewCount();
        model.addAttribute("topPosts", topPosts);

        return "main/main";
    }

}
