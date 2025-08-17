package com.example.TeamKDT.DTO;

import lombok.Data;

@Data
public class TeamViewDTO {
    private Long teamId;
    private String teamName;
    private int maxMembers;
    private String regionName;
    private String teamDescription;
    private String teamImage;
    private String gameDay;
    private boolean isCaptain;
}
