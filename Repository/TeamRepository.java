package com.example.TeamKDT.Repository;

import com.example.TeamKDT.Entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByTeamNameContaining(String keyword);
}


