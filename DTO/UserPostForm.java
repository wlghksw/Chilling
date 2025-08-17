package com.example.TeamKDT.DTO;

import lombok.Data;

import java.util.List;

@Data
public class UserPostForm {
    private Long userId;
    private String nickname;
    private List<PostDTO> posts;
}
