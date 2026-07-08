package com.courtsync.repository;

import com.courtsync.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.date >= CURRENT_DATE " +
           "AND r.status <> 'CANCELLED' ORDER BY r.date ASC, r.startTime ASC")
    List<Reservation> findUpcomingByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.date < CURRENT_DATE " +
           "ORDER BY r.date DESC, r.startTime DESC")
    List<Reservation> findPastByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.hall.id = :hallId AND r.date = :date " +
           "AND r.status <> 'CANCELLED'")
    long countTodayByHallId(@Param("hallId") Long hallId, @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(TIMESTAMPDIFF(HOUR, r.startTime, r.endTime)), 0) " +
           "FROM Reservation r WHERE r.user.id = :userId AND r.status = 'COMPLETED'")
    Double sumHoursByUserId(@Param("userId") Long userId);
}
