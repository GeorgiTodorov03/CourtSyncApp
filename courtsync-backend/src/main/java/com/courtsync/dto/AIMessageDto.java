package com.courtsync.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AIMessageDto {
    private Long conversationId;
    private String role;
    private String content;
    private String timestamp;
    private List<SportHallDto> suggestedHalls;
}
