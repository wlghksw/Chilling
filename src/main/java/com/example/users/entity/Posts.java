package com.example.users.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name="posts")
@Getter
@Setter
@ToString
public class Posts {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="post_id")
    private Integer post_id;

    @Column(name="user_id", nullable=false)
    private Integer user_id;

    @Column(name="category_id", nullable=false)
    private Integer category_id;

    @Column(name="sport_id")
    private Integer sport_id;

    @Column(name="title", length=200, nullable=false)
    private String title;

    @Lob
    @Column(name="content", columnDefinition="TEXT")
    private String content;

    @Column(name="view_count", nullable=false)
    private Integer view_count;

    @Column(name="like_count", nullable=false)
    private Integer like_count;

    @Column(name="is_notice", nullable=false)
    private Boolean is_notice;

    @Column(name="created_at", nullable=false)
    private LocalDateTime created_at;

    @Column(name="updated_at", nullable=false)
    private LocalDateTime updated_at;

    @Column(name="is_active", nullable=false)
    private Boolean is_active;


    @PrePersist
    void prePersist(){
        if(view_count==null) view_count=0;
        if(like_count==null) like_count=0;
        if(is_notice==null) is_notice=false;
        if(is_active==null) is_active=true;
        if(created_at==null) created_at=LocalDateTime.now();
        if(updated_at==null) updated_at=LocalDateTime.now();
    }
    @PreUpdate
    void preUpdate(){
        updated_at=LocalDateTime.now();
    }


}
