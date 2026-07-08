package com.courtsync.controller;

import com.courtsync.dto.*;
import com.courtsync.service.AIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @PostMapping("/chat")
    public ResponseEntity<AIMessageDto> sendMessage(
            @Valid @RequestBody AIMessageRequest request,
            Authentication auth) {
        return ResponseEntity.ok(aiService.sendMessage(request, auth.getName()));
    }
}
