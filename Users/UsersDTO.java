package com.example.users.dto;

import com.example.users.entity.Users.Gender;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UsersDTO {

    private String login_id;
    private String password;
    private String real_name;
    private String nickname;
    private String phone;
    private String birth_year;
    private String gender;
    private String profile_image;
}
