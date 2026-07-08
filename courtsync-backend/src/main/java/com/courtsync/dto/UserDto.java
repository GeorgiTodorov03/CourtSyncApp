package com.courtsync.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class UserDto {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String city;
    private String profileImageUrl;
    private String role;
    private BigDecimal credits;
    private Integer totalBookings;
    private Double totalHours;
}
