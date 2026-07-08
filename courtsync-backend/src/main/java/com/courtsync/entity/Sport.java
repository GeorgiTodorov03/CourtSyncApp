package com.courtsync.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "sports")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Sport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 100)
    private String iconName;

    @OneToMany(mappedBy = "sport", fetch = FetchType.LAZY)
    private List<SportHall> halls;
}
