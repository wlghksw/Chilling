package com.example.KDT.project.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "post_categories")
public class PostCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    @Column(nullable = false)
    private String categoryName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType categoryType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id")
    private Sport sport;

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    // Post 엔티티와 1:N 관계 매핑
    // "mappedBy" 값은 Post 엔티티에 있는 필드 이름과 일치해야 함
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;


    // 이 부분이 PostCategory 클래스 파일에 함께 정의되어 있어야 합니다.
    public enum CategoryType {
        introduction, community, NOTICE, notice
    }


}
