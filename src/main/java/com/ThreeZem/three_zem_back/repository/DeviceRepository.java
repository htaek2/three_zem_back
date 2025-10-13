package com.ThreeZem.three_zem_back.repository;

import com.ThreeZem.three_zem_back.data.entity.Building;
import com.ThreeZem.three_zem_back.data.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByFloorId(Long floorId);
    List<Device> findByFloorBuilding(Building building);
    List<Device> findByFloorFloorNum(int floorNum);
}
