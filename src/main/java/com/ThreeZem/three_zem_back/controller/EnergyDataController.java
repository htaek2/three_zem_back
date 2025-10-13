package com.ThreeZem.three_zem_back.controller;

import com.ThreeZem.three_zem_back.data.dto.energy.EnergyRangeRequestDto;
import com.ThreeZem.three_zem_back.data.dto.energy.EnergyReadingsDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalTime;

@RestController
public class EnergyDataController {

    //<editor-fold desc="Electricity">
    /// 시간 단위 전력 사용량 조회
    @GetMapping("/api/data/elec/hour")
    public ResponseEntity<EnergyReadingsDto> getElectricityHourData(@RequestBody EnergyRangeRequestDto energyRangeRequestDto) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new EnergyReadingsDto());
    }

    /// 일 단위 전력 사용량 조회
    @GetMapping("/api/data/elec/day")
    public ResponseEntity<EnergyReadingsDto> getEelctricityDayData(@RequestBody EnergyRangeRequestDto energyRangeRequestDto) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new EnergyReadingsDto());
    }

    /// 월 단위 전력 사용량 조회
    @GetMapping("/api/data/elec/month")
    public ResponseEntity<EnergyReadingsDto> getElectricityMonthData(@RequestBody EnergyRangeRequestDto energyRangeRequestDto) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new EnergyReadingsDto());
    }

    /// 년 단위 전력 사용량 조회
    @GetMapping("/api/data/elec/year")
    public ResponseEntity<EnergyReadingsDto> getElectricityYearData(@RequestBody EnergyRangeRequestDto energyRangeRequestDto) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new EnergyReadingsDto());
    }
    //</editor-fold>

    //<editor-fold desc="Gas">
    /// 시간 단위 가스 사용량 조회
    @GetMapping("/api/data/gas/hour")
    public ResponseEntity<EnergyReadingsDto> getGasHourData(@RequestBody EnergyRangeRequestDto energyRangeRequestDto) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new EnergyReadingsDto());
    }

    /// 일 단위 가스 사용량 조회
    @GetMapping("/api/data/gas/day")
    public ResponseEntity<EnergyReadingsDto> getGasDayData(@RequestBody EnergyRangeRequestDto energyRangeRequestDto) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new EnergyReadingsDto());
    }

    /// 월 단위 가스 사용량 조회
    @GetMapping("/api/data/gas/month")
    public ResponseEntity<EnergyReadingsDto> getGasMonthData(@RequestBody EnergyRangeRequestDto energyRangeRequestDto) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new EnergyReadingsDto());
    }

    /// 년 단위 가스 사용량 조회
    @GetMapping("/api/data/gas/year")
    public ResponseEntity<EnergyReadingsDto> getGasYearData(@RequestBody EnergyRangeRequestDto energyRangeRequestDto) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new EnergyReadingsDto());
    }
    //</editor-fold>

    //<editor-fold desc="Water">
    /// 시간 단위 수도 사용량 조회
    @GetMapping("/api/data/water/hour")
    public ResponseEntity<EnergyReadingsDto> getWaterHourData(@RequestBody EnergyRangeRequestDto energyRangeRequestDto) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new EnergyReadingsDto());
    }

    /// 일 단위 수도 사용량 조회
    @GetMapping("/api/data/water/day")
    public ResponseEntity<EnergyReadingsDto> getWaterDayData(@RequestBody EnergyRangeRequestDto energyRangeRequestDto) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new EnergyReadingsDto());
    }

    /// 월 단위 수도 사용량 조회
    @GetMapping("/api/data/water/month")
    public ResponseEntity<EnergyReadingsDto> getWaterMonthData(@RequestBody EnergyRangeRequestDto energyRangeRequestDto) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new EnergyReadingsDto());
    }

    /// 년 단위 수도 사용량 조회
    @GetMapping("/api/data/water/year")
    public ResponseEntity<EnergyReadingsDto> getWaterYearData(@RequestBody EnergyRangeRequestDto energyRangeRequestDto) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new EnergyReadingsDto());
    }
    //</editor-fold>

}
