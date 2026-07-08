package com.courtsync.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReservationRequest {
    @NotNull
    private Long hallId;
    @NotNull
    private String date;
    @NotNull
    private String startTime;
    @NotNull
    private String endTime;
}
