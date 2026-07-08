package com.courtsync.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class SportHallDto {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String district;
    private Double latitude;
    private Double longitude;
    private BigDecimal pricePerHour;
    private String openTime;
    private String closeTime;
    private Double rating;
    private Integer reviewCount;
    private String imageUrl;
    private String hallType;
    private String sportName;
    private Long sportId;
    private Integer bookingsToday;
    private Boolean isFavorite;
    private Double distanceKm;
}
