package com.example.TeamKDT.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Post(String title, User user) {
        this.title = title;
        this.user = user;
    }

    public User getUserId() {
        return user;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}





