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
    
    Optional<MatchApplication> findByMatchMatchIdAndPositionAndStatus(Long matchId, String position, MatchApplication.ApplicationStatus status);

    // 특정 매치의 특정 포지션에 대한 승인된 신청 수 조회
    long countByMatchMatchIdAndPositionAndStatus(Long matchId, String position, MatchApplication.ApplicationStatus status);
}
