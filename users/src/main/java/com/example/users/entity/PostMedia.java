package com.example.users.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_media")
@Getter @Setter
public class PostMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "media_id")
    private Integer mediaId;

    @Column(name = "post_id", nullable = false)
    private Integer postId;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false, length = 10)
    private MediaType mediaType;   // image | video
    public enum MediaType { image, video }

    @Column(name = "media_url", nullable = false, length = 255)
    private String mediaUrl;

    @Column(name = "media_order", nullable = false)
    private Integer mediaOrder;

    // DB DEFAULT CURRENT_TIMESTAMP 사용 (JPA는 값 안 넣고 DB가 채움)
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 혹시라도 서비스에서 안 넣었을 때의 최후 보루
    @PrePersist
    void prePersist() {
        if (mediaOrder == null) mediaOrder = 1;
    }
}
