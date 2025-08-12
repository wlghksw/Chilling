package com.example.KDT.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "regions")
@Data
public class Region {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    private Long regionId;
    
    @Column(name = "region_name", nullable = false, length = 100)
    private String regionName;
    
    @Column(name = "region_code", nullable = false, length = 20, unique = true)
    private String regionCode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cheonan_region_id")
    private Region parentRegion;
    
    @OneToMany(mappedBy = "parentRegion", cascade = CascadeType.ALL)
    private List<Region> subRegions;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
