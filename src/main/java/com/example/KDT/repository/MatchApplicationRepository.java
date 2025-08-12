package com.example.KDT.repository;

import com.example.KDT.entity.MatchApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchApplicationRepository extends JpaRepository<MatchApplication, Long> {
    
    List<MatchApplication> findByMatchMatchId(Long matchId);
    
    List<MatchApplication> findByUserId(Long userId);
    
    Optional<MatchApplication> findByMatchMatchIdAndUserId(Long matchId, Long userId);
    
    @Query("SELECT ma FROM MatchApplication ma WHERE ma.match.matchId = :matchId AND ma.status = 'APPROVED'")
    List<MatchApplication> findApprovedApplicationsByMatch(@Param("matchId") Long matchId);
    
    @Query("SELECT ma FROM MatchApplication ma WHERE ma.match.matchId = :matchId AND ma.status = 'PENDING'")
    List<MatchApplication> findPendingApplicationsByMatch(@Param("matchId") Long matchId);
}
