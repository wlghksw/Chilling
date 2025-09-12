package com.example.KDT.service;

import com.example.KDT.entity.Match;
import com.example.KDT.entity.MatchApplication;
import com.example.KDT.entity.User;
import com.example.KDT.entity.Venue;
import com.example.KDT.entity.Sport;
import com.example.KDT.repository.MatchRepository;
import com.example.KDT.repository.MatchApplicationRepository;
import com.example.KDT.repository.VenueRepository;
import com.example.KDT.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {
    
    private final MatchRepository matchRepository;
    private final MatchApplicationRepository matchApplicationRepository;
    private final VenueRepository venueRepository;
    private final SportRepository sportRepository;
    
    // 축구 포지션별 가격 설정
    private static final Map<String, BigDecimal> SOCCER_POSITION_PRICES = Map.of(
        "공격수", new BigDecimal("8000"),
        "미드필더", new BigDecimal("6000"),
        "수비수", new BigDecimal("4000"),
        "골키퍼", new BigDecimal("0")
    );
    
    // 축구 포지션 목록
    private static final List<String> SOCCER_POSITIONS = List.of(
        "공격수", "미드필더", "수비수", "골키퍼"
    );
    
    // 축구 포지션별 최대 인원 설정
    private static final Map<String, Integer> SOCCER_POSITION_MAX_PLAYERS = Map.of(
        "공격수", 6,    // 홈팀 3명 + 원정팀 3명
        "미드필더", 6,  // 홈팀 3명 + 원정팀 3명  
        "수비수", 8,    // 홈팀 4명 + 원정팀 4명
        "골키퍼", 2     // 홈팀 1명 + 원정팀 1명
    );
    
    // 풋살 포지션별 가격 설정
    private static final Map<String, BigDecimal> FUTSAL_POSITION_PRICES = Map.of(
        "공격수", new BigDecimal("6000"),
        "미드필더", new BigDecimal("5000"),
        "골키퍼", new BigDecimal("0")
    );
    
    // 풋살 포지션 목록
    private static final List<String> FUTSAL_POSITIONS = List.of(
        "공격수", "미드필더", "골키퍼"
    );
    
    // 풋살 포지션별 최대 인원 설정
    private static final Map<String, Integer> FUTSAL_POSITION_MAX_PLAYERS = Map.of(
        "공격수", 4,    // 홈팀 2명 + 원정팀 2명
        "미드필더", 4,  // 홈팀 2명 + 원정팀 2명
        "골키퍼", 2     // 홈팀 1명 + 원정팀 1명
    );
    
    // 야구 포지션별 가격 설정
    private static final Map<String, BigDecimal> BASEBALL_POSITION_PRICES = Map.of(
        "투수", new BigDecimal("7000"),
        "포수", new BigDecimal("6000"),
        "내야수", new BigDecimal("5000"),
        "외야수", new BigDecimal("5000"),
        "지명타자", new BigDecimal("4000")
    );
    
    // 야구 포지션 목록
    private static final List<String> BASEBALL_POSITIONS = List.of(
        "투수", "포수", "내야수", "외야수", "지명타자"
    );
    
    // 야구 포지션별 최대 인원 설정
    private static final Map<String, Integer> BASEBALL_POSITION_MAX_PLAYERS = Map.of(
        "투수", 2,      // 홈팀 1명 + 원정팀 1명
        "포수", 2,      // 홈팀 1명 + 원정팀 1명
        "내야수", 6,    // 홈팀 3명 + 원정팀 3명
        "외야수", 6,    // 홈팀 3명 + 원정팀 3명
        "지명타자", 2   // 홈팀 1명 + 원정팀 1명
    );
    
    // 테니스 포지션별 가격 설정
    private static final Map<String, BigDecimal> TENNIS_POSITION_PRICES = Map.of(
        "싱글스", new BigDecimal("8000"),
        "더블스", new BigDecimal("6000")
    );
    
    // 테니스 포지션 목록
    private static final List<String> TENNIS_POSITIONS = List.of(
        "싱글스", "더블스"
    );
    
    // 테니스 포지션별 최대 인원 설정
    private static final Map<String, Integer> TENNIS_POSITION_MAX_PLAYERS = Map.of(
        "싱글스", 2,
        "더블스", 2
    );
    
    // 축구 포지션별 가격 조회
    public Map<String, BigDecimal> getSoccerPositionPrices() {
        return new HashMap<>(SOCCER_POSITION_PRICES);
    }
    
    // 축구 포지션 목록 조회
    public List<String> getSoccerPositions() {
        return new ArrayList<>(SOCCER_POSITIONS);
    }
    
    // 축구 포지션별 최대 인원 조회
    public Map<String, Integer> getSoccerPositionMaxPlayers() {
        return new HashMap<>(SOCCER_POSITION_MAX_PLAYERS);
    }
    
    // 풋살 포지션별 가격 조회
    public Map<String, BigDecimal> getFutsalPositionPrices() {
        return new HashMap<>(FUTSAL_POSITION_PRICES);
    }
    
    // 풋살 포지션 목록 조회
    public List<String> getFutsalPositions() {
        return new ArrayList<>(FUTSAL_POSITIONS);
    }
    
    // 풋살 포지션별 최대 인원 조회
    public Map<String, Integer> getFutsalPositionMaxPlayers() {
        return new HashMap<>(FUTSAL_POSITION_MAX_PLAYERS);
    }
    
    // 야구 포지션별 가격 조회
    public Map<String, BigDecimal> getBaseballPositionPrices() {
        return new HashMap<>(BASEBALL_POSITION_PRICES);
    }
    
    // 야구 포지션 목록 조회
    public List<String> getBaseballPositions() {
        return new ArrayList<>(BASEBALL_POSITIONS);
    }
    
    // 테니스 포지션별 가격 조회
    public Map<String, BigDecimal> getTennisPositionPrices() {
        return new HashMap<>(TENNIS_POSITION_PRICES);
    }
    
    // 테니스 포지션 목록 조회
    public List<String> getTennisPositions() {
        return new ArrayList<>(TENNIS_POSITIONS);
    }
    
    // 포지션별 가격 계산 (스포츠별)
    public BigDecimal calculatePositionPrice(String sportName, String position) {
        if ("축구".equals(sportName) && SOCCER_POSITION_PRICES.containsKey(position)) {
            return SOCCER_POSITION_PRICES.get(position);
        } else if ("풋살".equals(sportName) && FUTSAL_POSITION_PRICES.containsKey(position)) {
            return FUTSAL_POSITION_PRICES.get(position);
        } else if ("야구".equals(sportName) && BASEBALL_POSITION_PRICES.containsKey(position)) {
            return BASEBALL_POSITION_PRICES.get(position);
        } else if ("테니스".equals(sportName) && TENNIS_POSITION_PRICES.containsKey(position)) {
            return TENNIS_POSITION_PRICES.get(position);
        }
        return BigDecimal.ZERO;
    }
    
    // 매치 목록 조회 - 스포츠별로 분류
    public List<Match> getAllMatches() {
        return matchRepository.findAllWithSportAndVenue();
    }
    
    // 스포츠별 매치 조회
    public List<Match> getMatchesBySport(Long sportId) {
        return matchRepository.findBySportIdWithSportAndVenue(sportId);
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
    public MatchApplication applyForMatch(Long matchId, User user, String position) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("매치를 찾을 수 없습니다."));
        
        // 이미 신청했는지 확인
        Optional<MatchApplication> existingApplication = matchApplicationRepository
                .findByMatchMatchIdAndUserId(matchId, user.getUserId());
        
        if (existingApplication.isPresent()) {
            throw new RuntimeException("이미 신청한 매치입니다.");
        }
        
        // 해당 포지션의 현재 신청 인원 확인
        long currentPositionCount = matchApplicationRepository
                .countByMatchMatchIdAndPositionAndStatus(matchId, position, MatchApplication.ApplicationStatus.APPROVED);
        
        // 포지션별 최대 인원 제한 확인
        String sportName = match.getSport().getSportName();
        int maxPositionPlayers = getMaxPositionPlayers(sportName, position);
        
        if (maxPositionPlayers > 0 && currentPositionCount >= maxPositionPlayers) {
            throw new RuntimeException("해당 포지션은 최대 인원에 도달했습니다.");
        }
        
        // 매치가 가득 찼는지 확인
        if (match.getCurrentPlayers() >= match.getMaxPlayers()) {
            throw new RuntimeException("매치가 가득 찼습니다.");
        }
        
        // 포지션별 가격 자동 설정
        BigDecimal price = calculatePositionPrice(match.getSport().getSportName(), position);
        
        MatchApplication application = new MatchApplication();
        application.setMatch(match);
        application.setMatch(match);
        application.setUser(user);
        application.setPosition(position);
        application.setStatus(MatchApplication.ApplicationStatus.APPROVED); // 즉시 승인
        application.setPrice(price);
        
        // 매치 현재 인원 증가
        match.setCurrentPlayers(match.getCurrentPlayers() + 1);
        
        // 매치가 가득 찼으면 상태 변경
        if (match.getCurrentPlayers() >= match.getMaxPlayers()) {
            match.setStatus(Match.MatchStatus.FULL);
        }
        
        matchRepository.save(match);
        
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
    
    // 구장 목록 조회
    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }
    
    // 구장 ID로 구장 정보 조회
    public Venue getVenueById(Long venueId) {
        return venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("구장을 찾을 수 없습니다."));
    }
    
    // 스포츠 목록 조회
    public List<Sport> getAllSports() {
        return sportRepository.findAll();
    }
    
    // 매치 수정
    @Transactional
    public Match updateMatch(Match match) {
        Match existingMatch = matchRepository.findById(match.getMatchId())
                .orElseThrow(() -> new RuntimeException("매치를 찾을 수 없습니다."));
        
        existingMatch.setVenue(match.getVenue());
        existingMatch.setSport(match.getSport());
        existingMatch.setMatchDate(match.getMatchDate());
        existingMatch.setStartTime(match.getStartTime());
        existingMatch.setEndTime(match.getEndTime());
        existingMatch.setMaxPlayers(match.getMaxPlayers());
        existingMatch.setDescription(match.getDescription());
        existingMatch.setStatus(match.getStatus());
        existingMatch.setUpdatedAt(LocalDateTime.now());
        
        return matchRepository.save(existingMatch);
    }
    
    // 매치 삭제
    @Transactional
    public void deleteMatch(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("매치를 찾을 수 없습니다."));
        
        // 매치 신청이 있으면 먼저 삭제
        List<MatchApplication> applications = matchApplicationRepository.findByMatchMatchId(matchId);
        if (!applications.isEmpty()) {
            matchApplicationRepository.deleteAll(applications);
        }
        
        // 매치 삭제
        matchRepository.delete(match);
    }
    
    // 매치별 신청자 목록 조회
    public List<MatchApplication> getMatchApplications(Long matchId) {
        return matchApplicationRepository.findByMatchMatchId(matchId);
    }
    
    // 모든 매치 신청 조회
    public List<MatchApplication> getAllMatchApplications() {
        return matchApplicationRepository.findAll();
    }
    
    // 포지션별 신청 현황 조회 (축구용)
    public Map<String, String> getSoccerPositionStatus(Long matchId) {
        List<MatchApplication> applications = matchApplicationRepository.findByMatchMatchId(matchId);
        Map<String, String> positionStatus = new HashMap<>();
        
        // 기본 포지션 설정
        positionStatus.put("GK1", ""); // 홈팀 골키퍼
        positionStatus.put("GK2", ""); // 원정팀 골키퍼
        positionStatus.put("DF1", ""); // 홈팀 수비수1
        positionStatus.put("DF2", ""); // 홈팀 수비수2
        positionStatus.put("DF3", ""); // 홈팀 수비수3
        positionStatus.put("DF4", ""); // 홈팀 수비수4
        positionStatus.put("DF5", ""); // 원정팀 수비수1
        positionStatus.put("DF6", ""); // 원정팀 수비수2
        positionStatus.put("DF7", ""); // 원정팀 수비수3
        positionStatus.put("DF8", ""); // 원정팀 수비수4
        positionStatus.put("MF1", ""); // 홈팀 미드필더1
        positionStatus.put("MF2", ""); // 홈팀 미드필더2
        positionStatus.put("MF3", ""); // 홈팀 미드필더3
        positionStatus.put("MF4", ""); // 원정팀 미드필더1
        positionStatus.put("MF5", ""); // 원정팀 미드필더2
        positionStatus.put("MF6", ""); // 원정팀 미드필더3
        positionStatus.put("FW1", ""); // 홈팀 공격수1
        positionStatus.put("FW2", ""); // 홈팀 공격수2
        positionStatus.put("FW3", ""); // 홈팀 공격수3
        positionStatus.put("FW4", ""); // 원정팀 공격수1
        positionStatus.put("FW5", ""); // 원정팀 공격수2
        positionStatus.put("FW6", ""); // 원정팀 공격수3
        
        // 승인된 신청만 처리
        for (MatchApplication app : applications) {
            if (app.getStatus() == MatchApplication.ApplicationStatus.APPROVED) {
                String position = app.getPosition();
                String nickname = app.getUser().getNickname();
                
                // 포지션에 따라 적절한 슬롯에 배치
                if ("골키퍼".equals(position)) {
                    if (positionStatus.get("GK1").isEmpty()) {
                        positionStatus.put("GK1", nickname);
                    } else if (positionStatus.get("GK2").isEmpty()) {
                        positionStatus.put("GK2", nickname);
                    }
                } else if ("수비수".equals(position)) {
                    for (int i = 1; i <= 8; i++) {
                        String key = "DF" + i;
                        if (positionStatus.get(key).isEmpty()) {
                            positionStatus.put(key, nickname);
                            break;
                        }
                    }
                } else if ("미드필더".equals(position)) {
                    for (int i = 1; i <= 6; i++) {
                        String key = "MF" + i;
                        if (positionStatus.get(key).isEmpty()) {
                            positionStatus.put(key, nickname);
                            break;
                        }
                    }
                } else if ("공격수".equals(position)) {
                    for (int i = 1; i <= 6; i++) {
                        String key = "FW" + i;
                        if (positionStatus.get(key).isEmpty()) {
                            positionStatus.put(key, nickname);
                            break;
                        }
                    }
                }
            }
        }
        
        return positionStatus;
    }
    
    // 포지션별 신청 현황 조회 (풋살용)
    public Map<String, String> getFutsalPositionStatus(Long matchId) {
        List<MatchApplication> applications = matchApplicationRepository.findByMatchMatchId(matchId);
        Map<String, String> positionStatus = new HashMap<>();
        
        // 기본 포지션 설정 (5v5 풋살)
        positionStatus.put("GK1", ""); // 홈팀 골키퍼
        positionStatus.put("GK2", ""); // 원정팀 골키퍼
        positionStatus.put("DF1", ""); // 홈팀 수비수1
        positionStatus.put("DF2", ""); // 홈팀 수비수2
        positionStatus.put("DF3", ""); // 원정팀 수비수1
        positionStatus.put("DF4", ""); // 원정팀 수비수2
        positionStatus.put("MF1", ""); // 홈팀 미드필더
        positionStatus.put("MF2", ""); // 원정팀 미드필더
        positionStatus.put("FW1", ""); // 홈팀 공격수
        positionStatus.put("FW2", ""); // 원정팀 공격수
        
        // 승인된 신청만 처리
        for (MatchApplication app : applications) {
            if (app.getStatus() == MatchApplication.ApplicationStatus.APPROVED) {
                String position = app.getPosition();
                String nickname = app.getUser().getNickname();
                
                // 포지션에 따라 적절한 슬롯에 배치
                if ("골키퍼".equals(position)) {
                    if (positionStatus.get("GK1").isEmpty()) {
                        positionStatus.put("GK1", nickname);
                    } else if (positionStatus.get("GK2").isEmpty()) {
                        positionStatus.put("GK2", nickname);
                    }
                } else if ("수비수".equals(position)) {
                    for (int i = 1; i <= 4; i++) {
                        String key = "DF" + i;
                        if (positionStatus.get(key).isEmpty()) {
                            positionStatus.put(key, nickname);
                            break;
                        }
                    }
                } else if ("미드필더".equals(position)) {
                    for (int i = 1; i <= 2; i++) {
                        String key = "MF" + i;
                        if (positionStatus.get(key).isEmpty()) {
                            positionStatus.put(key, nickname);
                            break;
                        }
                    }
                } else if ("공격수".equals(position)) {
                    for (int i = 1; i <= 2; i++) {
                        String key = "FW" + i;
                        if (positionStatus.get(key).isEmpty()) {
                            positionStatus.put(key, nickname);
                            break;
                        }
                    }
                }
            }
        }
        
        return positionStatus;
    }
    
    // 포지션별 신청 현황 조회 (야구용)
    public Map<String, String> getBaseballPositionStatus(Long matchId) {
        // 데이터베이스 연결 상태 확인
        System.out.println("=== 야구 포지션 상태 조회 시작 ===");
        System.out.println("매치 ID: " + matchId);
        
        try {
            List<MatchApplication> applications = matchApplicationRepository.findByMatchMatchId(matchId);
            Map<String, String> positionStatus = new HashMap<>();
            
            // 디버깅: 실제 조회된 신청 데이터 확인
            System.out.println("조회된 신청 수: " + applications.size());
            
            // 데이터베이스에서 실제로 조회되는 내용 확인
            if (applications.isEmpty()) {
                System.out.println("✅ 신청 데이터가 없습니다 - 모든 포지션은 빈 상태여야 함");
            } else {
                System.out.println("❌ 신청 데이터가 있습니다:");
                for (MatchApplication app : applications) {
                    System.out.println("  - 포지션: " + app.getPosition() + 
                                    ", 상태: " + app.getStatus() + 
                                    ", 사용자: " + (app.getUser() != null ? app.getUser().getNickname() : "NULL") +
                                    ", 신청ID: " + app.getApplicationId() +
                                    ", 매치ID: " + app.getMatch().getMatchId());
                }
            }
            
            // 기본 포지션 설정 (홈팀 9명 + 원정팀 9명 = 총 18명)
            // 홈팀 포지션
            positionStatus.put("P", "");   // 홈팀 투수
            positionStatus.put("C", "");   // 홈팀 포수
            positionStatus.put("1B", "");  // 홈팀 1루수
            positionStatus.put("2B", "");  // 홈팀 2루수
            positionStatus.put("3B", "");  // 홈팀 3루수
            positionStatus.put("SS", "");  // 홈팀 유격수
            positionStatus.put("LF", "");  // 홈팀 좌익수
            positionStatus.put("CF", "");  // 홈팀 중견수
            positionStatus.put("RF", "");  // 홈팀 우익수
            
            // 원정팀 포지션
            positionStatus.put("P2", "");  // 원정팀 투수
            positionStatus.put("C2", "");  // 원정팀 포수
            positionStatus.put("1B2", ""); // 원정팀 1루수
            positionStatus.put("2B2", ""); // 원정팀 2루수
            positionStatus.put("3B2", ""); // 원정팀 3루수
            positionStatus.put("SS2", ""); // 원정팀 유격수
            positionStatus.put("LF2", ""); // 원정팀 좌익수
            positionStatus.put("CF2", ""); // 원정팀 중견수
            positionStatus.put("RF2", ""); // 원정팀 우익수
            
            // 승인된 신청만 처리
            for (MatchApplication app : applications) {
                if (app.getStatus() == MatchApplication.ApplicationStatus.APPROVED) {
                    String position = app.getPosition();
                    String nickname = app.getUser() != null ? app.getUser().getNickname() : "알 수 없음";
                    
                    // 신청 폼에서 선택한 구체적인 포지션 값을 그대로 사용
                    if (positionStatus.containsKey(position)) {
                        positionStatus.put(position, nickname);
                        System.out.println("✅ 포지션 " + position + "에 " + nickname + " 배정됨");
                    } else {
                        System.out.println("⚠️ 알 수 없는 포지션: " + position);
                    }
                } else {
                    System.out.println("⚠️ 승인되지 않은 신청: " + app.getPosition() + " - " + app.getStatus());
                }
            }
            
            System.out.println("최종 포지션 상태: " + positionStatus);
            System.out.println("=== 야구 포지션 상태 조회 완료 ===\n");
            return positionStatus;
            
        } catch (Exception e) {
            System.err.println("❌ 야구 포지션 상태 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    
    // 포지션별 신청 현황 조회 (테니스용)
    public Map<String, String> getTennisPositionStatus(Long matchId) {
        List<MatchApplication> applications = matchApplicationRepository.findByMatchMatchId(matchId);
        Map<String, String> positionStatus = new HashMap<>();
        
        // 기본 포지션 설정 (4명 테니스 - 2v2 더블스)
        positionStatus.put("HOME1", "");  // 홈팀 플레이어1
        positionStatus.put("HOME2", "");  // 홈팀 플레이어2
        positionStatus.put("AWAY1", "");  // 원정팀 플레이어1
        positionStatus.put("AWAY2", "");  // 원정팀 플레이어2
        
        // 승인된 신청만 처리
        for (MatchApplication app : applications) {
            if (app.getStatus() == MatchApplication.ApplicationStatus.APPROVED) {
                String position = app.getPosition();
                String nickname = app.getUser() != null ? app.getUser().getNickname() : "알 수 없음";
                
                // 포지션에 직접 매핑
                if (positionStatus.containsKey(position)) {
                    positionStatus.put(position, nickname);
                }
            }
        }
        
        return positionStatus;
    }
    
    // 스포츠와 포지션에 따른 최대 인원 반환
    private int getMaxPositionPlayers(String sportName, String position) {
        switch (sportName) {
            case "축구":
                return SOCCER_POSITION_MAX_PLAYERS.getOrDefault(position, 0);
            case "풋살":
                return FUTSAL_POSITION_MAX_PLAYERS.getOrDefault(position, 0);
            case "야구":
                return BASEBALL_POSITION_MAX_PLAYERS.getOrDefault(position, 0);
            case "테니스":
                return TENNIS_POSITION_MAX_PLAYERS.getOrDefault(position, 0);
            default:
                return 0;
        }
    }
    
    // 사용자가 특정 매치에 신청했는지 확인
    public boolean hasUserAppliedForMatch(Long matchId, Long userId) {
        Optional<MatchApplication> application = matchApplicationRepository.findByMatchMatchIdAndUserId(matchId, userId);
        return application.isPresent();
    }
    
    // 사용자의 특정 매치에 대한 신청 정보 조회
    public List<MatchApplication> getUserApplicationsForMatch(Long matchId, Long userId) {
        Optional<MatchApplication> application = matchApplicationRepository.findByMatchMatchIdAndUserId(matchId, userId);
        if (application.isPresent()) {
            return List.of(application.get());
        }
        return List.of();
    }
    
    // 사용자의 매치 신청 취소
    @Transactional
    public void cancelMatchApplication(Long matchId, Long userId) {
        System.out.println("=== MatchService.cancelMatchApplication 시작 ===");
        System.out.println("매치 ID: " + matchId + ", 사용자 ID: " + userId);
        
        Optional<MatchApplication> applicationOpt = matchApplicationRepository.findByMatchMatchIdAndUserId(matchId, userId);
        
        if (applicationOpt.isEmpty()) {
            System.out.println("취소할 매치 신청을 찾을 수 없음");
            throw new RuntimeException("취소할 매치 신청을 찾을 수 없습니다.");
        }
        
        MatchApplication application = applicationOpt.get();
        System.out.println("찾은 신청: ID=" + application.getApplicationId() + 
                          ", 포지션=" + application.getPosition() + 
                          ", 상태=" + application.getStatus());
        
        matchApplicationRepository.delete(application);
        System.out.println("매치 신청 삭제 완료");
        
        // 매치의 현재 참가자 수 감소
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("매치를 찾을 수 없습니다."));
        
        int currentPlayers = match.getCurrentPlayers();
        System.out.println("현재 참가자 수: " + currentPlayers);
        
        if (currentPlayers > 0) {
            match.setCurrentPlayers(currentPlayers - 1);
            matchRepository.save(match);
            System.out.println("참가자 수 감소 완료: " + (currentPlayers - 1));
        }
        
        System.out.println("=== MatchService.cancelMatchApplication 완료 ===");
    }
}
