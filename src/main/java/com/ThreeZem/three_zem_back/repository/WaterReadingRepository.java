package com.ThreeZem.three_zem_back.repository;

import com.ThreeZem.three_zem_back.data.entity.WaterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WaterReadingRepository extends JpaRepository<WaterReading, Long> {
    List<WaterReading> findByReadingTimeBetween(LocalDateTime start, LocalDateTime end);
    @Query("SELECT wr FROM WaterReading wr WHERE wr.floor.floorNum = :floorNum AND wr.readingTime BETWEEN :start AND :end")
    List<WaterReading> findByFloor_FloorNumAndReadingTimeBetween(@Param("floorNum") int floor, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
