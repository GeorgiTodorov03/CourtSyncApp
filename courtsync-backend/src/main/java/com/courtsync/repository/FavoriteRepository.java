package com.courtsync.repository;

import com.courtsync.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserIdAndHallId(Long userId, Long hallId);
    boolean existsByUserIdAndHallId(Long userId, Long hallId);
    void deleteByUserIdAndHallId(Long userId, Long hallId);
}
