package com.ThreeZem.three_zem_back.repository;

import com.ThreeZem.three_zem_back.data.entity.ElectricityMonthlyUsage;
import com.ThreeZem.three_zem_back.data.entity.GasMonthlyUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface GasMonthlyUsageRepository extends JpaRepository<GasMonthlyUsage, Long> {
    List<GasMonthlyUsage> findByBuildingIdAndTimestampBetween(Long billingId, LocalDateTime timestamp1, LocalDateTime timestamp2);
}
