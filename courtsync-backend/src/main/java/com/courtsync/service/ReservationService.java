package com.courtsync.service;

import com.courtsync.dto.*;
import com.courtsync.entity.*;
import com.courtsync.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SportHallRepository hallRepository;
    private final UserRepository userRepository;

    public List<ReservationDto> getUpcoming(Long userId) {
        return reservationRepository.findUpcomingByUserId(userId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<ReservationDto> getPast(Long userId) {
        return reservationRepository.findPastByUserId(userId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public ReservationDto create(CreateReservationRequest request, Long userId) {
        SportHall hall = hallRepository.findById(request.getHallId())
                .orElseThrow(() -> new RuntimeException("Hall not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate date = LocalDate.parse(request.getDate());
        LocalTime start = LocalTime.parse(request.getStartTime());
        LocalTime end = LocalTime.parse(request.getEndTime());

        long hours = ChronoUnit.HOURS.between(start, end);
        BigDecimal total = hall.getPricePerHour().multiply(BigDecimal.valueOf(hours));

        Reservation reservation = Reservation.builder()
                .user(user)
                .hall(hall)
                .date(date)
                .startTime(start)
                .endTime(end)
                .totalPrice(total)
                .build();

        return mapToDto(reservationRepository.save(reservation));
    }

    @Transactional
    public void cancel(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        if (!reservation.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized");
        }
        reservation.setStatus(Reservation.Status.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        reservationRepository.save(reservation);
    }

    private ReservationDto mapToDto(Reservation r) {
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String relativeDate = getRelativeDate(r.getDate());

        return ReservationDto.builder()
                .id(r.getId())
                .hallId(r.getHall().getId())
                .hallName(r.getHall().getName())
                .hallImageUrl(r.getHall().getImageUrl())
                .sportName(r.getHall().getSport() != null ? r.getHall().getSport().getName() : "")
                .date(r.getDate().format(dateFmt))
                .startTime(r.getStartTime().toString())
                .endTime(r.getEndTime().toString())
                .totalPrice(r.getTotalPrice())
                .status(r.getStatus().name())
                .relativeDate(relativeDate)
                .build();
    }

    private String getRelativeDate(LocalDate date) {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), date);
        if (days == 0) return "Today";
        if (days == 1) return "Tomorrow";
        if (days < 7) return "Coming up in " + days + " days";
        if (days < 14) return "Next week";
        return "In " + (days / 7) + " weeks";
    }
}
