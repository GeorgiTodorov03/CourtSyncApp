package com.courtsync.service;

import com.courtsync.dto.*;
import com.courtsync.entity.User;
import com.courtsync.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    public UserDto getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        int totalBookings = reservationRepository.findUpcomingByUserId(user.getId()).size() +
                            reservationRepository.findPastByUserId(user.getId()).size();
        Double hours = reservationRepository.sumHoursByUserId(user.getId());
        return AuthService.mapToDto(user, totalBookings, hours != null ? hours : 0.0);
    }

    @Transactional
    public UserDto updateProfile(String email, UpdateUserRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getProfileImageUrl() != null) user.setProfileImageUrl(request.getProfileImageUrl());
        userRepository.save(user);
        return AuthService.mapToDto(user, 0, 0.0);
    }
}
