package com.ThreeZem.three_zem_back.repository;

import com.ThreeZem.three_zem_back.data.entity.WaterReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WaterReadingRepository extends JpaRepository<WaterReading, Long> {
    Optional<WaterReading> findByFloorId(Long floorId);
    List<WaterReading> findByReadingTimeBetween(LocalDateTime start, LocalDateTime end);
}
