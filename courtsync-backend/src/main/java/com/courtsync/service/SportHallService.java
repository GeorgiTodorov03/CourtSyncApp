package com.courtsync.service;

import com.courtsync.dto.SportHallDto;
import com.courtsync.entity.*;
import com.courtsync.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SportHallService {

    private final SportHallRepository hallRepository;
    private final FavoriteRepository favoriteRepository;
    private final ReservationRepository reservationRepository;

    public List<SportHallDto> getRecommended(Long userId) {
        List<SportHall> halls = hallRepository.findRecommended(PageRequest.of(0, 10));
        return halls.stream().map(h -> mapToDto(h, userId)).collect(Collectors.toList());
    }

    public Page<SportHallDto> search(String query, Long sportId, String sortBy, int page, int size, Long userId) {
        Sort sort = switch (sortBy != null ? sortBy : "rating_desc") {
            case "rating_asc" -> Sort.by("rating").ascending();
            case "rating_desc", "rating" -> Sort.by("rating").descending();
            case "price_asc" -> Sort.by("pricePerHour").ascending();
            case "price_desc" -> Sort.by("pricePerHour").descending();
            case "name_asc" -> Sort.by("name").ascending();
            case "name_desc" -> Sort.by("name").descending();
            default -> Sort.by("rating").descending();
        };
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SportHall> halls = hallRepository.search(
                (query != null && !query.isBlank()) ? query : null,
                sportId,
                pageable
        );
        return halls.map(h -> mapToDto(h, userId));
    }

    public SportHallDto getById(Long id, Long userId) {
        SportHall hall = hallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hall not found"));
        return mapToDto(hall, userId);
    }

    @Transactional
    public void toggleFavorite(Long hallId, Long userId) {
        if (favoriteRepository.existsByUserIdAndHallId(userId, hallId)) {
            favoriteRepository.deleteByUserIdAndHallId(userId, hallId);
        } else {
            SportHall hall = hallRepository.findById(hallId)
                    .orElseThrow(() -> new RuntimeException("Hall not found"));
            User user = new User();
            user.setId(userId);
            favoriteRepository.save(Favorite.builder().user(user).hall(hall).build());
        }
    }

    private SportHallDto mapToDto(SportHall hall, Long userId) {
        long bookingsToday = reservationRepository.countTodayByHallId(hall.getId(), LocalDate.now());
        boolean isFavorite = userId != null && favoriteRepository.existsByUserIdAndHallId(userId, hall.getId());

        return SportHallDto.builder()
                .id(hall.getId())
                .name(hall.getName())
                .description(hall.getDescription())
                .address(hall.getAddress())
                .city(hall.getCity())
                .district(hall.getDistrict())
                .latitude(hall.getLatitude())
                .longitude(hall.getLongitude())
                .pricePerHour(hall.getPricePerHour())
                .openTime(hall.getOpenTime() != null ? hall.getOpenTime().toString() : null)
                .closeTime(hall.getCloseTime() != null ? hall.getCloseTime().toString() : null)
                .rating(hall.getRating())
                .reviewCount(hall.getReviewCount())
                .imageUrl(hall.getImageUrl())
                .hallType(hall.getHallType() != null ? hall.getHallType().name() : null)
                .sportName(hall.getSport() != null ? hall.getSport().getName() : null)
                .sportId(hall.getSport() != null ? hall.getSport().getId() : null)
                .bookingsToday((int) bookingsToday)
                .isFavorite(isFavorite)
                .build();
    }
}
