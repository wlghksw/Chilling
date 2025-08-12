package com.example.KDT.service;

import com.example.KDT.entity.Match;
import com.example.KDT.entity.MatchApplication;
import com.example.KDT.entity.User;
import com.example.KDT.entity.Venue;
import com.example.KDT.repository.MatchRepository;
import com.example.KDT.repository.MatchApplicationRepository;
import com.example.KDT.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {
    
    private final MatchRepository matchRepository;
    private final MatchApplicationRepository matchApplicationRepository;
    private final VenueRepository venueRepository;
    
    // 매치 목록 조회 - 스포츠별로 분류
    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }
    
    // 스포츠별 매치 조회
    public List<Match> getMatchesBySport(Long sportId) {
        return matchRepository.findBySportSportId(sportId);
    }
    

    
    // 매치 상세 조회
    public Optional<Match> getMatchById(Long matchId) {
        return matchRepository.findById(matchId);
    }
    
    // 매치 생성
    @Transactional
    public Match createMatch(Match match) {
        match.setStatus(Match.MatchStatus.OPEN);
        return matchRepository.save(match);
    }
    
    // 매치 신청
    @Transactional
    public MatchApplication applyForMatch(Long matchId, User user, String position, java.math.BigDecimal price) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("매치를 찾을 수 없습니다."));
        
        // 이미 신청했는지 확인
        Optional<MatchApplication> existingApplication = matchApplicationRepository
                .findByMatchMatchIdAndUserId(matchId, user.getUserId());
        
        if (existingApplication.isPresent()) {
            throw new RuntimeException("이미 신청한 매치입니다.");
        }
        
        // 매치가 가득 찼는지 확인
        if (match.getCurrentPlayers() >= match.getMaxPlayers()) {
            throw new RuntimeException("매치가 가득 찼습니다.");
        }
        
        MatchApplication application = new MatchApplication();
        application.setMatch(match);
        application.setUser(user);
        application.setPosition(position);
        application.setPrice(price);
        
        return matchApplicationRepository.save(application);
    }
    
    // 매치 신청 승인
    @Transactional
    public void approveApplication(Long applicationId) {
        MatchApplication application = matchApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("신청을 찾을 수 없습니다."));
        
        application.setStatus(MatchApplication.ApplicationStatus.APPROVED);
        matchApplicationRepository.save(application);
        
        // 매치 현재 인원 증가
        Match match = application.getMatch();
        match.setCurrentPlayers(match.getCurrentPlayers() + 1);
        
        // 매치가 가득 찼으면 상태 변경
        if (match.getCurrentPlayers() >= match.getMaxPlayers()) {
            match.setStatus(Match.MatchStatus.FULL);
        }
        
        matchRepository.save(match);
    }
    
    // 매치 신청 거절
    @Transactional
    public void rejectApplication(Long applicationId) {
        MatchApplication application = matchApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("신청을 찾을 수 없습니다."));
        
        application.setStatus(MatchApplication.ApplicationStatus.REJECTED);
        matchApplicationRepository.save(application);
    }
    

    
    // 매치 통계 조회
    public Map<String, Object> getMatchStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 전체 매치 수
        List<Match> allMatches = matchRepository.findAll();
        stats.put("totalMatches", allMatches.size());
        
        // 스포츠별 매치 수
        Map<String, Long> matchesBySport = allMatches.stream()
                .collect(Collectors.groupingBy(
                    match -> match.getSport().getSportName(),
                    Collectors.counting()
                ));
        stats.put("matchesBySport", matchesBySport);
        
        // 지역별 매치 수
        Map<String, Long> matchesByRegion = allMatches.stream()
                .collect(Collectors.groupingBy(
                    match -> match.getVenue().getRegion().getRegionName(),
                    Collectors.counting()
                ));
        stats.put("matchesByRegion", matchesByRegion);
        
        // 상태별 매치 수
        Map<String, Long> matchesByStatus = allMatches.stream()
                .collect(Collectors.groupingBy(
                    match -> match.getStatus().name(),
                    Collectors.counting()
                ));
        stats.put("matchesByStatus", matchesByStatus);
        
        return stats;
    }
}
