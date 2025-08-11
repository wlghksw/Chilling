package com.example.KDT.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "posts")
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonIgnore
    private User author;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    public String getAuthorUsername() {
        return (author != null) ? author.getUsername() : null;
    }
    public String getCreatedAtFmt() {
        return (createdAt != null) ? createdAt.toString().replace('T',' ') : null;
    }
    public String getUpdatedAtFmt() {
        return (updatedAt != null) ? updatedAt.toString().replace('T',' ') : null;
    }
    public String getContentPreview() {
        if (content == null) return "";
        return content.length() <= 100 ? content : content.substring(0,100) + "...";
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
