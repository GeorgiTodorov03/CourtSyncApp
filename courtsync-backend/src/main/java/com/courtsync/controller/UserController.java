package com.courtsync.controller;

import com.courtsync.dto.*;
import com.courtsync.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getProfile(Authentication auth) {
        return ResponseEntity.ok(userService.getProfile(auth.getName()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateProfile(
            @RequestBody UpdateUserRequest request,
            Authentication auth) {
        return ResponseEntity.ok(userService.updateProfile(auth.getName(), request));
    }
}
