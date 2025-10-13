package com.ThreeZem.three_zem_back.repository;

import com.ThreeZem.three_zem_back.data.entity.Building;
import com.ThreeZem.three_zem_back.data.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FloorRepository extends JpaRepository<Floor, Long> {
    Optional<Floor> findByBuildingId(UUID buildingId);

    List<Floor> findByBuilding(Building building);
}
