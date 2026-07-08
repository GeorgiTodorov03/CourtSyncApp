package com.courtsync.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AIMessageRequest {
    @NotBlank
    private String message;
    private Long conversationId;
}
