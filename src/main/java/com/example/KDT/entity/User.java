package com.example.KDT.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "login_id", unique = true, length = 50, nullable = false)
    private String loginId;

    @Column(name = "nickname", unique = true, length = 50, nullable = false)
    private String nickname;

    @Column(name = "email")
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "real_name", length = 100, nullable = false)
    private String realName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "birth_year")
    private Integer birthYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Column(name = "profile_image", length = 255)
    private String profileImage;

    @Column(name = "is_admin", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isAdmin = false;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Transient
    private Integer postCount;

    public enum Gender {
        male, female
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 화면/보안 호환용 파생 게터
    public String getUsername() {
        return nickname != null ? nickname : loginId;
    }

    // 화면용 통일된 표시 이름
    public String getDisplayName() {
        return nickname != null ? nickname : loginId;
    }
    
    // ID getter 메서드
    public Long getUserId() {
        return id;
    }
    
    // ID setter 메서드
    public void setUserId(Long userId) {
        this.id = userId;
    }
}
