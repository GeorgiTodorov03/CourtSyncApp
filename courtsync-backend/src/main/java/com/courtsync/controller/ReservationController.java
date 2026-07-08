package com.courtsync.controller;

import com.courtsync.dto.*;
import com.courtsync.repository.UserRepository;
import com.courtsync.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final UserRepository userRepository;

    @GetMapping("/upcoming")
    public ResponseEntity<List<ReservationDto>> getUpcoming(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(reservationService.getUpcoming(userId));
    }

    @GetMapping("/past")
    public ResponseEntity<List<ReservationDto>> getPast(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(reservationService.getPast(userId));
    }

    @PostMapping
    public ResponseEntity<ReservationDto> create(
            @Valid @RequestBody CreateReservationRequest request,
            Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(reservationService.create(request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        reservationService.cancel(id, userId);
        return ResponseEntity.ok().build();
    }

    private Long getUserId(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
    }
}
