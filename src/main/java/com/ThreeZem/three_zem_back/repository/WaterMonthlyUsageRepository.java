package com.ThreeZem.three_zem_back.repository;

import com.ThreeZem.three_zem_back.data.entity.ElectricityMonthlyUsage;
import com.ThreeZem.three_zem_back.data.entity.WaterMonthlyUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WaterMonthlyUsageRepository extends JpaRepository<WaterMonthlyUsage, Long> {
    List<WaterMonthlyUsage> findByBuildingIdAndTimestampBetween(Long billingId, LocalDateTime timestamp1, LocalDateTime timestamp2);
}
