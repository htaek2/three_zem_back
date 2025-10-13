package com.ThreeZem.three_zem_back.repository;

import com.ThreeZem.three_zem_back.data.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BuildingRepository extends JpaRepository<Building, UUID> {
    Optional<Building> findByMemberId(Long memberId);
}
