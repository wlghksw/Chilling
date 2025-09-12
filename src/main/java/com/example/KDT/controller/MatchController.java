package com.example.KDT.controller;

import com.example.KDT.entity.*;
import com.example.KDT.service.MatchService;
import com.example.KDT.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/match")
@RequiredArgsConstructor
public class MatchController {
    
    private final MatchService matchService;
    private final UserService userService;
    
    // 현재 로그인한 사용자 정보 가져오기
    private User getCurrentUser() {
        try {
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            if (currentUsername != null && !"anonymousUser".equals(currentUsername)) {
                return userService.getUserByLoginId(currentUsername)
                        .orElse(null);
            }
        } catch (Exception e) {
            System.out.println("Spring Security 컨텍스트 오류: " + e.getMessage());
        }
        return null;
    }
    
    // 매치 메인 페이지 (루트 경로)
    @GetMapping("")
    public String matchMain() {
        return "redirect:/match/list";
    }
    
    // 매치 목록 페이지
    @GetMapping("/list")
    public String matchList(Model model, HttpSession session) {
        List<Match> matches = matchService.getAllMatches();
        model.addAttribute("matches", matches);
        model.addAttribute("sportId", null); // 전체 보기
        
        // 로그인된 사용자 정보 전달 (데이터베이스에서 최신 정보 가져오기)
        User loggedInUser = getCurrentUser();
        if (loggedInUser != null) {
            model.addAttribute("loggedInUser", loggedInUser);
        }
        
        return "match/list";
    }
    
    // 스포츠별 매치 목록
    @GetMapping("/sport/{sportId}")
    public String matchesBySport(@PathVariable Long sportId, Model model, HttpSession session) {
        List<Match> matches = matchService.getMatchesBySport(sportId);
        model.addAttribute("matches", matches);
        model.addAttribute("sportId", sportId);
        
        // 로그인된 사용자 정보 전달 (데이터베이스에서 최신 정보 가져오기)
        User loggedInUser = getCurrentUser();
        if (loggedInUser != null) {
            model.addAttribute("loggedInUser", loggedInUser);
        }
        
        return "match/list";
    }
    

    
    // 매치 상세 페이지
    @GetMapping("/{matchId}")
    public String matchDetail(@PathVariable Long matchId, Model model, HttpSession session) {
        Match match = matchService.getMatchById(matchId)
                .orElseThrow(() -> new RuntimeException("매치를 찾을 수 없습니다."));
        
        // 현재 로그인한 사용자 정보 추가 (데이터베이스에서 최신 정보 가져오기)
        User loggedInUser = getCurrentUser();
        if (loggedInUser != null) {
            model.addAttribute("loggedInUser", loggedInUser);
            // 현재 사용자가 매치 작성자인지 확인
            boolean isAuthor = loggedInUser.getUserId().equals(match.getCreatedBy().getUserId());
            model.addAttribute("isAuthor", isAuthor);
        } else {
            model.addAttribute("isAuthor", false);
        }
        
        // 매치 관련 정보들을 모델에 추가
        model.addAttribute("match", match);
        model.addAttribute("matchId", match.getMatchId());
        model.addAttribute("matchDate", match.getMatchDate());
        model.addAttribute("startTime", match.getStartTime());
        model.addAttribute("endTime", match.getEndTime());
        model.addAttribute("maxPlayers", match.getMaxPlayers());
        model.addAttribute("currentPlayers", match.getCurrentPlayers());
        model.addAttribute("description", match.getDescription());
        model.addAttribute("status", match.getStatus());
        
        // venue와 sport 정보 추가
        if (match.getVenue() != null) {
            model.addAttribute("venue", match.getVenue());
        }
        if (match.getSport() != null) {
            model.addAttribute("sport", match.getSport());
        }
        return "match/detail";
    }
    
    // 매치 신청 처리
    @PostMapping("/{matchId}/apply")
    public String applyForMatch(@PathVariable Long matchId,
                               @RequestParam String position,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            // 로그인된 사용자 정보 확인 (데이터베이스에서 최신 정보 가져오기)
            User loggedInUser = getCurrentUser();
            if (loggedInUser == null) {
                redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
                return "redirect:/login";
            }
            
            // 매치 신청 생성 (이미 승인됨)
            MatchApplication application = matchService.applyForMatch(matchId, loggedInUser, position);
            
            // 성공 메시지와 함께 목록으로 리다이렉트
            redirectAttributes.addFlashAttribute("successMessage", 
                loggedInUser.getNickname() + "님이 " + position + " 포지션으로 매치에 참여했습니다!");
            return "redirect:/match/list";
        } catch (Exception e) {
            // 에러 메시지와 함께 신청 폼으로 리다이렉트
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
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
    
    // 매치 등록 폼 (관리자만)
    @GetMapping("/create")
    public String createForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // 관리자 권한 체크 (데이터베이스에서 최신 정보 가져오기)
        User loggedInUser = getCurrentUser();
        if (loggedInUser == null || !Boolean.TRUE.equals(loggedInUser.getIsAdmin())) {
            redirectAttributes.addFlashAttribute("error", "관리자만 매치를 등록할 수 있습니다.");
            return "redirect:/match/list";
        }
        
        List<Venue> venues = matchService.getAllVenues();
        List<Sport> sports = matchService.getAllSports();
        model.addAttribute("venues", venues);
        model.addAttribute("sports", sports);
        model.addAttribute("match", new Match());
        return "match/create";
    }
    
    // 매치 등록 처리 (관리자만)
    @PostMapping("/create")
    public String createMatch(@ModelAttribute Match match, 
                             @RequestParam String matchDateStr,
                             @RequestParam String startTimeStr,
                             @RequestParam String endTimeStr,
                             Model model,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        // 관리자 권한 체크 (데이터베이스에서 최신 정보 가져오기)
        User loggedInUser = getCurrentUser();
        if (loggedInUser == null || !Boolean.TRUE.equals(loggedInUser.getIsAdmin())) {
            redirectAttributes.addFlashAttribute("error", "관리자만 매치를 등록할 수 있습니다.");
            return "redirect:/match/list";
        }
        
        try {
            // 날짜와 시간 파싱
            if (matchDateStr != null && !matchDateStr.isEmpty()) {
                match.setMatchDate(java.time.LocalDate.parse(matchDateStr));
            }
            if (startTimeStr != null && !startTimeStr.isEmpty()) {
                match.setStartTime(java.time.LocalTime.parse(startTimeStr));
            }
            if (endTimeStr != null && !endTimeStr.isEmpty()) {
                match.setEndTime(java.time.LocalTime.parse(endTimeStr));
            }
            
            match.setCreatedBy(loggedInUser);
            match.setStatus(Match.MatchStatus.OPEN);
            match.setCurrentPlayers(0);
            
            matchService.createMatch(match);
            redirectAttributes.addFlashAttribute("successMessage", "매치가 성공적으로 등록되었습니다.");
            return "redirect:/match/list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            List<Venue> venues = matchService.getAllVenues();
            List<Sport> sports = matchService.getAllSports();
            model.addAttribute("venues", venues);
            model.addAttribute("sports", sports);
            return "match/create";
        }
    }
    
    // 매치 수정 폼 (관리자만)
    @GetMapping("/{matchId}/edit")
    public String editForm(@PathVariable Long matchId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // 관리자 권한 체크 (데이터베이스에서 최신 정보 가져오기)
        User loggedInUser = getCurrentUser();
        if (loggedInUser == null || !Boolean.TRUE.equals(loggedInUser.getIsAdmin())) {
            redirectAttributes.addFlashAttribute("error", "관리자만 매치를 수정할 수 있습니다.");
            return "redirect:/match/" + matchId;
        }
        
        Match match = matchService.getMatchById(matchId)
                .orElseThrow(() -> new RuntimeException("매치를 찾을 수 없습니다."));
        
        List<Venue> venues = matchService.getAllVenues();
        List<Sport> sports = matchService.getAllSports();
        
        model.addAttribute("match", match);
        model.addAttribute("venues", venues);
        model.addAttribute("sports", sports);
        return "match/edit";
    }
    
    // 매치 수정 처리 (관리자만)
    @PostMapping("/{matchId}/edit")
    public String editMatch(@PathVariable Long matchId, 
                           @ModelAttribute Match match,
                           Model model,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        // 관리자 권한 체크 (데이터베이스에서 최신 정보 가져오기)
        User loggedInUser = getCurrentUser();
        if (loggedInUser == null || !Boolean.TRUE.equals(loggedInUser.getIsAdmin())) {
            redirectAttributes.addFlashAttribute("error", "관리자만 매치를 수정할 수 있습니다.");
            return "redirect:/match/" + matchId;
        }
        
        try {
            match.setMatchId(matchId);
            matchService.updateMatch(match);
            redirectAttributes.addFlashAttribute("successMessage", "매치가 성공적으로 수정되었습니다.");
            return "redirect:/match/" + matchId;
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            List<Venue> venues = matchService.getAllVenues();
            List<Sport> sports = matchService.getAllSports();
            model.addAttribute("venues", venues);
            model.addAttribute("sports", sports);
            return "match/edit";
        }
    }
    
    // 매치 삭제 처리 (관리자만)
    @PostMapping("/{matchId}/delete")
    public String deleteMatch(@PathVariable Long matchId, 
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        // 관리자 권한 체크 (데이터베이스에서 최신 정보 가져오기)
        User loggedInUser = getCurrentUser();
        if (loggedInUser == null || !Boolean.TRUE.equals(loggedInUser.getIsAdmin())) {
            redirectAttributes.addFlashAttribute("error", "관리자만 매치를 삭제할 수 있습니다.");
            return "redirect:/match/" + matchId;
        }
        
        try {
            matchService.deleteMatch(matchId);
            redirectAttributes.addFlashAttribute("successMessage", "매치가 성공적으로 삭제되었습니다.");
            return "redirect:/match/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "매치 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/match/" + matchId;
        }
    }

    // 구장 선택 시 스포츠 정보 가져오기
    @GetMapping("/venue/{venueId}/sport")
    @ResponseBody
    public Sport getSportByVenue(@PathVariable Long venueId) {
        System.out.println("=== 구장 ID로 스포츠 조회 요청: " + venueId + " ===");
        Venue venue = matchService.getVenueById(venueId);
        Sport sport = venue.getSport();
        System.out.println("=== 구장: " + venue.getVenueName() + ", 스포츠: " + (sport != null ? sport.getSportName() : "null") + " ===");
        return sport;
    }
    
    @GetMapping("/{matchId}/apply")
    public String applyForm(@PathVariable Long matchId, Model model) {
        Match match = matchService.getMatchById(matchId)
                .orElseThrow(() -> new RuntimeException("매치를 찾을 수 없습니다."));
        
        model.addAttribute("match", match);
        model.addAttribute("venue", match.getVenue());
        model.addAttribute("matchDate", match.getMatchDate());
        model.addAttribute("startTime", match.getStartTime());
        model.addAttribute("endTime", match.getEndTime());
        model.addAttribute("currentPlayers", match.getCurrentPlayers());
        model.addAttribute("maxPlayers", match.getMaxPlayers());
        
        // 스포츠별 포지션 정보와 신청 현황 추가
        String sportCode = match.getSport().getSportCode();
        String sportName = match.getSport().getSportName();
        
        switch (sportCode) {
            case "SOCCER":
                model.addAttribute("soccerPositions", matchService.getSoccerPositions());
                model.addAttribute("soccerPositionPrices", matchService.getSoccerPositionPrices());
                model.addAttribute("positionStatus", matchService.getSoccerPositionStatus(matchId));
                break;
            case "FUTSAL":
                model.addAttribute("futsalPositions", matchService.getFutsalPositions());
                model.addAttribute("futsalPositionPrices", matchService.getFutsalPositionPrices());
                model.addAttribute("positionStatus", matchService.getFutsalPositionStatus(matchId));
                break;
            case "BASEBALL":
                model.addAttribute("baseballPositions", matchService.getBaseballPositions());
                model.addAttribute("baseballPositionPrices", matchService.getBaseballPositionPrices());
                model.addAttribute("positionStatus", matchService.getBaseballPositionStatus(matchId));
                break;
            case "TENNIS":
                model.addAttribute("tennisPositions", matchService.getTennisPositions());
                model.addAttribute("tennisPositionPrices", matchService.getTennisPositionPrices());
                model.addAttribute("positionStatus", matchService.getTennisPositionStatus(matchId));
                break;
        }
        
        // 스포츠별 신청 양식 템플릿 결정
        String templateName = "match/apply";
        
        switch (sportCode) {
            case "SOCCER":
                templateName = "match/apply-soccer";
                break;
            case "FUTSAL":
                templateName = "match/apply-futsal";
                break;
            case "BASEBALL":
                templateName = "match/apply-baseball";
                break;
            case "TENNIS":
                templateName = "match/apply-tennis";
                break;
            default:
                templateName = "match/apply";
        }
        
        return templateName;
    }
}
