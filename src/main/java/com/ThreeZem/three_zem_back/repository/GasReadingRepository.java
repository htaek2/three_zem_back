package com.ThreeZem.three_zem_back.repository;

import com.ThreeZem.three_zem_back.data.entity.GasReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GasReadingRepository extends JpaRepository<GasReading, Long> {
    Optional<GasReading> findByBuildingId(UUID buildingId);
}
