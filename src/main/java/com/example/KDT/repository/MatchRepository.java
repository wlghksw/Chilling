package com.example.KDT.repository;

import com.example.KDT.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    
    List<Match> findBySportSportId(Long sportId);
    
    List<Match> findByVenueVenueId(Long venueId);
    
    @Query("SELECT m FROM Match m JOIN FETCH m.venue v JOIN FETCH v.region JOIN FETCH m.sport WHERE m.matchDate = :matchDate")
    List<Match> findByMatchDate(@Param("matchDate") LocalDate matchDate);
    
    @Query("SELECT m FROM Match m JOIN FETCH m.venue v JOIN FETCH v.region JOIN FETCH m.sport WHERE m.matchDate >= :matchDate")
    List<Match> findByMatchDateGreaterThanEqual(@Param("matchDate") LocalDate matchDate);
    
    @Query("SELECT m FROM Match m JOIN FETCH m.venue v JOIN FETCH v.region JOIN FETCH m.sport WHERE m.sport.sportId = :sportId AND m.matchDate >= :date AND m.status = 'OPEN'")
    List<Match> findAvailableMatchesBySport(@Param("sportId") Long sportId, @Param("date") LocalDate date);
    
    @Query("SELECT m FROM Match m JOIN FETCH m.venue v JOIN FETCH v.region JOIN FETCH m.sport WHERE m.venue.venueId = :venueId AND m.matchDate >= :date")
    List<Match> findMatchesByVenueAndDate(@Param("venueId") Long venueId, @Param("date") LocalDate date);

    // 생성자 ID로 매치 조회
    List<Match> findByCreatedBy_Id(Long userId);
    
    // 스포츠와 경기장 정보를 함께 로드하는 매치 목록 조회
    @Query("SELECT m FROM Match m JOIN FETCH m.sport JOIN FETCH m.venue v JOIN FETCH v.region")
    List<Match> findAllWithSportAndVenue();
    
    // 특정 스포츠의 매치를 스포츠와 경기장 정보와 함께 로드
    @Query("SELECT m FROM Match m JOIN FETCH m.sport JOIN FETCH m.venue v JOIN FETCH v.region WHERE m.sport.sportId = :sportId")
    List<Match> findBySportIdWithSportAndVenue(@Param("sportId") Long sportId);
}
