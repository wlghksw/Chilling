package com.example.users.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer comment_id;

    @Column(nullable = false)
    private Integer post_id;

    @Column(nullable = false)
    private Integer user_id;


    @Column
    private Integer parent_comment_id;  // 대댓글이 아니면 null

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;


    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    @Column(nullable = false)
    private Boolean is_active;

    @PrePersist
    void prePersist() {
        if (created_at == null) created_at = LocalDateTime.now();
        if (updated_at == null) updated_at = created_at;
        if (is_active == null) is_active = true;
    }

    @PreUpdate
    void preUpdate() {

        updated_at = LocalDateTime.now();
    }

    @Transient   // DB 컬럼 아님
    public boolean isReply() {
        return parent_comment_id != null;
    }

    @Transient
    public boolean getIsReply() { // Mustache 호환용
        return isReply();
    }
}
