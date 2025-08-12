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
}
