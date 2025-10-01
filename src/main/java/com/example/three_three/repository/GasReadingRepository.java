package com.example.three_three.repository;

import com.example.three_three.entity.GasReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GasReadingRepository extends JpaRepository<GasReading, Integer> {
}
