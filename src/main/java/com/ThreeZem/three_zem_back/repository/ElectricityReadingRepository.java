package com.ThreeZem.three_zem_back.repository;

import com.ThreeZem.three_zem_back.data.entity.ElectricityReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ElectricityReadingRepository extends JpaRepository<ElectricityReading, Long> {
    Optional<ElectricityReading> findByDeviceId(Long deviceId);
    List<ElectricityReading> findByReadingTimeBetween(LocalDateTime start, LocalDateTime end);
}
