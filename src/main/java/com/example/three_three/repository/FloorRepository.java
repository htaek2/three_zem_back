package com.example.three_three.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.three_three.entity.Floor;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Integer>{
    Optional<List<Floor>> findByBuildingId(Integer buildingId);
    
}
