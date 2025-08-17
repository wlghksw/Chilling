package com.example.TeamKDT.DTO;

import com.example.TeamKDT.Entity.Post;
import com.example.TeamKDT.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long postId;
    private String title;
    private Long userId;

    private int index;

public PostDTO(Long postId,String title, Long userId) {
    this.postId = postId;
    this.title = title;
    this.userId = userId;
}

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Post toEntity(User user) {
        return new Post(this.title, user);
    }
}