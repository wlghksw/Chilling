package com.example.TeamKDT.DTO;

import com.example.TeamKDT.Entity.Post;
import com.example.TeamKDT.Entity.User;
import jakarta.persistence.Entity;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    private Long userId;
    private String username;
    private String nickname;
    private String password;

    public User toEntity(String rawPassword, PasswordEncoder encoder) {
        User user = new User();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setPassword(encoder.encode(rawPassword));
        return user;
    }

//    public UserDTO(Long userId, String username, String nickname) {
//        this.userId = userId;
//        this.username = username;
//        this.nickname = nickname;
//    }
}
