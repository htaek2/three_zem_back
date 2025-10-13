package com.ThreeZem.three_zem_back.repository;

import com.ThreeZem.three_zem_back.data.entity.TotalBill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TotalPriceRepository extends JpaRepository<TotalBill, Long> {
    Optional<TotalBill> findByBuildingId(UUID buildingId);
}
