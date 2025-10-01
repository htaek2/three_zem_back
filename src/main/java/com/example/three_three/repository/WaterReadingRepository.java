package com.example.three_three.repository;

import com.example.three_three.entity.WaterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WaterReadingRepository extends JpaRepository<WaterReading, Integer> {
}
