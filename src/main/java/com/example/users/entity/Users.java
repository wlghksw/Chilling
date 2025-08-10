package com.example.users.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    @Column(nullable = false, unique = true)
    private String login_id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String real_name;

    @Column(nullable = false, unique = true)
    private String nickname;

    private String phone;

    private Integer birth_year;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String profile_image;

    private LocalDateTime created_at = LocalDateTime.now();
    private LocalDateTime updated_at = LocalDateTime.now();

    private Boolean is_active = true;
    private Boolean is_admin = false;

    public enum Gender {
        male, female
    }
}
