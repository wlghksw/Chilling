package com.example.TeamKDT.Service;

import com.example.TeamKDT.DTO.TeamDTO;
import com.example.TeamKDT.Entity.Team;
import com.example.TeamKDT.Entity.User;
import com.example.TeamKDT.Repository.TeamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    public List<TeamDTO> getAllTeams(String keyword) {
        List<Team> teams;
        if (keyword == null || keyword.isEmpty()) {
            teams = teamRepository.findAll();
        } else {
            teams = teamRepository.findByTeamNameContaining(keyword);
        }
        return teams.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public void addTeam(TeamDTO dto, User currrentUser) throws IOException {
        Team team = new Team();
        team.setTeamName(dto.getTeamName());
        team.setMaxMembers(dto.getMaxMembers());
        team.setCurrentMembers(0);
        team.setGameDay(dto.getGameDay());
        team.setTeamDescription(dto.getTeamDescription());
        team.setTeamImage(dto.getTeamImage());
        team.setCaptain(currrentUser);

        if (dto.getTeamFile() != null && !dto.getTeamFile().isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + dto.getTeamFile().getOriginalFilename();
            Path uploadPath = Paths.get("uploads");
            Files.createDirectories(uploadPath);
            dto.getTeamFile().transferTo(uploadPath.resolve(fileName));
            team.setTeamImage("/uploads/" + fileName);
        }
        teamRepository.save(team);
    }

    // 팀 업데이트
    public void updateTeam(TeamDTO dto, User captain) {
        Team team = teamRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new RuntimeException("팀이 존재하지 않습니다."));
        team.setTeamName(dto.getTeamName());
        team.setMaxMembers(dto.getMaxMembers());
        team.setGameDay(dto.getGameDay());
        team.setTeamDescription(dto.getTeamDescription());
        team.setTeamImage(dto.getTeamImage());
        team.setCaptain(captain);
        teamRepository.save(team);
    }

    // 팀 삭제
    public void deleteTeam(Long id, User captain) {
        teamRepository.deleteById(id);
    }

    private TeamDTO toDTO(Team team) {
        TeamDTO dto = new TeamDTO();
        dto.setTeamId(team.getTeamId());
        dto.setTeamName(team.getTeamName());
        dto.setMaxMembers(team.getMaxMembers());
        dto.setGameDay(team.getGameDay());
        dto.setTeamDescription(team.getTeamDescription());
        dto.setTeamImage(team.getTeamImage());
        dto.setRegionName(team.getRegion() != null ? team.getRegion().getRegionName() : "");
        return dto;
    }

    public Team getTeamById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("팀이 존재하지 않습니다."));
    }


    public TeamDTO getTeamDTOById(Long id) {
        Team team = getTeamById(id);
        return toDTO(team); // 엔티티를 dto로
    }
}



