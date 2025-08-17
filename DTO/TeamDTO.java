package com.example.TeamKDT.DTO;

import org.springframework.web.multipart.MultipartFile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamDTO {
    private Long teamId;
    private String teamName;
    private Integer maxMembers;
    private Integer currentMembers;
    private String gameDay;
    private String teamDescription;
    private String regionName;

    // 이미지 업로드용(로고사진)
    private MultipartFile teamFile;

    // DB에 이미지 저장
    private String teamImage;

    public String getExistingImage() {
        return teamImage != null && !teamImage.isEmpty() ? teamImage : null;
    }
}

