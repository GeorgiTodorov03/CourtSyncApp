package com.courtsync.repository;

import com.courtsync.entity.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SportRepository extends JpaRepository<Sport, Long> {
    Optional<Sport> findByNameIgnoreCase(String name);
}
