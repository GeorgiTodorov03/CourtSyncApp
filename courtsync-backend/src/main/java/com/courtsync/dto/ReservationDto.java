package com.courtsync.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ReservationDto {
    private Long id;
    private Long hallId;
    private String hallName;
    private String hallImageUrl;
    private String sportName;
    private String date;
    private String startTime;
    private String endTime;
    private BigDecimal totalPrice;
    private String status;
    private String relativeDate;
}
