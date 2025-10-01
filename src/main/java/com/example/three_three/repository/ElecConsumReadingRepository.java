package com.example.three_three.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.three_three.entity.ElecConsumReading;

@Repository
public interface ElecConsumReadingRepository extends JpaRepository<ElecConsumReading, Integer> {
    
}
