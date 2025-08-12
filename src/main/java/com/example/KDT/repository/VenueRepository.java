package com.example.KDT.repository;

import com.example.KDT.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    
    @Query("SELECT v FROM Venue v JOIN FETCH v.region JOIN FETCH v.sport WHERE v.sport.sportId = :sportId")
    List<Venue> findBySportSportId(@Param("sportId") Long sportId);
    
    @Query("SELECT v FROM Venue v JOIN FETCH v.region JOIN FETCH v.sport WHERE v.region.regionId = :regionId")
    List<Venue> findByRegionRegionId(@Param("regionId") Long regionId);
    
    @Query("SELECT v FROM Venue v JOIN FETCH v.region JOIN FETCH v.sport WHERE v.sport.sportId = :sportId AND v.region.regionId = :regionId")
    List<Venue> findBySportAndRegion(@Param("sportId") Long sportId, @Param("regionId") Long regionId);
}
