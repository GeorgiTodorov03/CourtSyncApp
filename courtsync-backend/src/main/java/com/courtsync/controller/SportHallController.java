package com.courtsync.controller;

import com.courtsync.dto.SportHallDto;
import com.courtsync.repository.UserRepository;
import com.courtsync.service.SportHallService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/halls")
@RequiredArgsConstructor
public class SportHallController {

    private final SportHallService hallService;
    private final UserRepository userRepository;

    @GetMapping("/recommended")
    public ResponseEntity<List<SportHallDto>> getRecommended(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(hallService.getRecommended(userId));
    }

    @GetMapping
    public ResponseEntity<Page<SportHallDto>> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long sportId,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(hallService.search(query, sportId, sortBy, page, size, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SportHallDto> getById(@PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(hallService.getById(id, userId));
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<Void> toggleFavorite(@PathVariable Long id, Authentication auth) {
        Long userId = getRequiredUserId(auth);
        hallService.toggleFavorite(id, userId);
        return ResponseEntity.ok().build();
    }

    private Long getUserId(Authentication auth) {
        if (auth == null) return null;
        return userRepository.findByEmail(auth.getName()).map(u -> u.getId()).orElse(null);
    }

    private Long getRequiredUserId(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
    }
}
