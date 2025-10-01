package com.example.three_three.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.three_three.entity.Device;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Integer>{
    Optional<List<Device>> findByFloorId(Integer floorId);
    
}
