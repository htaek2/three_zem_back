package com.ThreeZem.three_zem_back.repository;

import com.ThreeZem.three_zem_back.data.entity.ElectricityReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ElectricityReadingRepository extends JpaRepository<ElectricityReading, Long> {
    Optional<ElectricityReading> findByDeviceId(Long deviceId);
    List<ElectricityReading> findByReadingTimeBetween(LocalDateTime start, LocalDateTime end);
    @Query("SELECT er FROM ElectricityReading er WHERE er.device.floor.floorNum = :floorNum AND er.readingTime BETWEEN :start AND :end")
    List<ElectricityReading> findByDevice_Floor_FloorNumAndReadingTimeBetween(@Param("floorNum") int floor, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT DISTINCT e.readingTime FROM ElectricityReading e WHERE e.readingTime BETWEEN :startTime AND :endTime")
    Set<LocalDateTime> findDistinctReadingTimeByReadingTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
