package com.ThreeZem.three_zem_back.repository;

import com.ThreeZem.three_zem_back.data.entity.ElectricityMonthlyUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ElectricityMonthlyUsageRepository extends JpaRepository<ElectricityMonthlyUsage, Long> {

    List<ElectricityMonthlyUsage> findByBuildingIdAndTimestampBetween(Long billingId, LocalDateTime timestamp1, LocalDateTime timestamp2);

}
