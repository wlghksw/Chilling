package com.example.TeamKDT.Controller;

import com.example.TeamKDT.DTO.TeamDTO;
import com.example.TeamKDT.Entity.User;
import com.example.TeamKDT.Repository.UserRepository;
import com.example.TeamKDT.Service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;
    private final UserRepository userRepository;

    // 팀 홈 페이지 + 검색
    @GetMapping("/home")
    public String teamHome(Model model, @RequestParam(required = false) String keyword) {
        List<TeamDTO> teams = teamService.getAllTeams(keyword);
        model.addAttribute("teams", teams);
        model.addAttribute("keyword", keyword != null ? keyword : "");

        return "team/home";
    }

    // 팀 생성 폼
    @GetMapping("/new")
    public String newTeamForm(Model model) {
        model.addAttribute("team", new TeamDTO());
        return "team/new";
    }

    // 팀 저장
    @PostMapping("/save")
    public String addTeam(@ModelAttribute TeamDTO dto) throws IOException {
        User currentUser = getCurrentUser();
        teamService.addTeam(dto, currentUser);
        return "redirect:/team/home";
    }

    // 팀 수정 폼
    @GetMapping("/{id}/edit")
    public String editTeamForm(@PathVariable Long id, Model model) {
        TeamDTO dto = teamService.getTeamDTOById(id);
        model.addAttribute("team", dto);
        return "team/edit";
    }

    // 팀 업데이트
    @PostMapping("/update")
    public String updateTeam(@ModelAttribute TeamDTO dto) {
        User currentUser = getCurrentUser();
        teamService.updateTeam(dto, currentUser);
        return "redirect:/team/home";
    }

    // 팀 삭제 (POST 처리)
    @PostMapping("/{id}/delete")
    public String deleteTeam(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        teamService.deleteTeam(id, currentUser);
        return "redirect:/team/home";
    }

    // 로그인한 사용자 가져오기
    private User getCurrentUser() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String username = auth.getName();
        return userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
    }
}



