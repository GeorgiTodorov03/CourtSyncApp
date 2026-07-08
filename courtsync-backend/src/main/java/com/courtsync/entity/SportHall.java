package com.courtsync.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "sport_halls")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SportHall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String district;

    private Double latitude;
    private Double longitude;

    @Column(precision = 8, scale = 2)
    private BigDecimal pricePerHour;

    @Column(nullable = false)
    @Builder.Default
    private LocalTime openTime = LocalTime.of(8, 0);

    @Column(nullable = false)
    @Builder.Default
    private LocalTime closeTime = LocalTime.of(23, 0);

    @Builder.Default
    private Double rating = 0.0;

    @Builder.Default
    private Integer reviewCount = 0;

    @Column(length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private HallType hallType = HallType.INDOOR;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sport_id")
    private Sport sport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Favorite> favorites;

    public enum HallType {
        INDOOR, OUTDOOR, PREMIUM, CLAY, GRASS
    }
}
