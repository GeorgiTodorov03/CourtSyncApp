package com.courtsync.repository;

import com.courtsync.entity.SportHall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SportHallRepository extends JpaRepository<SportHall, Long> {

    @Query("SELECT h FROM SportHall h WHERE h.active = true " +
           "AND (:query IS NULL OR LOWER(h.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "     OR LOWER(h.city) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "     OR LOWER(h.district) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND (:sportId IS NULL OR h.sport.id = :sportId)")
    Page<SportHall> search(@Param("query") String query,
                           @Param("sportId") Long sportId,
                           Pageable pageable);

    @Query("SELECT h FROM SportHall h WHERE h.active = true ORDER BY h.rating DESC")
    List<SportHall> findRecommended(Pageable pageable);

    List<SportHall> findBySportNameIgnoreCaseAndActiveTrue(String sportName);
}
