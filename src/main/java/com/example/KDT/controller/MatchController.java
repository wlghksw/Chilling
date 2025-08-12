package com.example.KDT.controller;

import com.example.KDT.entity.Match;
import com.example.KDT.entity.MatchApplication;
import com.example.KDT.entity.User;
import com.example.KDT.entity.Venue;
import com.example.KDT.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/match")
@RequiredArgsConstructor
public class MatchController {
    
    private final MatchService matchService;
    
    // 매치 메인 페이지 (루트 경로)
    @GetMapping("")
    public String matchMain() {
        return "redirect:/match/list";
    }
    
    // 매치 목록 페이지
    @GetMapping("/list")
    public String matchList(Model model) {
        List<Match> matches = matchService.getAllMatches();
        model.addAttribute("matches", matches);
        model.addAttribute("sportId", null); // 전체 보기
        return "match/list";
    }
    
    // 스포츠별 매치 목록
    @GetMapping("/sport/{sportId}")
    public String matchesBySport(@PathVariable Long sportId, Model model) {
        List<Match> matches = matchService.getMatchesBySport(sportId);
        model.addAttribute("matches", matches);
        model.addAttribute("sportId", sportId);
        return "match/list";
    }
    

    
    // 매치 상세 페이지
    @GetMapping("/{matchId}")
    public String matchDetail(@PathVariable Long matchId, Model model) {
        Match match = matchService.getMatchById(matchId)
                .orElseThrow(() -> new RuntimeException("매치를 찾을 수 없습니다."));
        model.addAttribute("match", match);
        return "match/detail";
    }
    
    // 매치 신청 폼
    @GetMapping("/{matchId}/apply")
    public String applyForm(@PathVariable Long matchId, Model model) {
        Match match = matchService.getMatchById(matchId)
                .orElseThrow(() -> new RuntimeException("매치를 찾을 수 없습니다."));
        model.addAttribute("match", match);
        return "match/apply";
    }
    
    // 매치 신청 처리
    @PostMapping("/{matchId}/apply")
    public String applyForMatch(@PathVariable Long matchId,
                               @RequestParam String position,
                               @RequestParam BigDecimal price,
                               Model model) {
        try {
            // TODO: 실제 로그인된 사용자 정보를 가져와야 함
            User user = new User(); // 임시 사용자 객체
            user.setUserId(1L); // 임시 ID
            
            MatchApplication application = matchService.applyForMatch(matchId, user, position, price);
            model.addAttribute("message", "매치 신청이 완료되었습니다.");
            return "redirect:/match/" + matchId;
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/match/" + matchId + "/apply";
        }
    }
    
    
    
    // 스포츠별 매치 통계
    @GetMapping("/stats")
    public String matchStats(Model model) {
        Map<String, Object> stats = matchService.getMatchStats();
        model.addAttribute("stats", stats);
        return "match/stats";
    }
}
