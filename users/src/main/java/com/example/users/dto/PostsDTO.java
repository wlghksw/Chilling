package com.example.users.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class PostsDTO {

    private Integer userId;
    private String title;
    private String content;
    private Integer categoryId; // 1=축구, 2=풋살, 3=야구, 4=테니스
    private List<MultipartFile> files;

}
