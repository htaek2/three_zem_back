package com.ThreeZem.three_zem_back.controller;

import com.ThreeZem.three_zem_back.data.dto.energy.EnergyReadingDto;
import com.ThreeZem.three_zem_back.service.EnergyDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EnergyDataController {

    private final EnergyDataService energyDataService;

    @GetMapping("/api/energy/elec")
    public ResponseEntity<EnergyReadingDto> getElecData(String start, String end, byte datetimeType) {
        EnergyReadingDto data = energyDataService.getElecRangeData(start, end, datetimeType);
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    @GetMapping("/api/energy/elec/{floor}")
    public ResponseEntity<EnergyReadingDto> getElecData(@PathVariable String floor, String start, String end, byte datetimeType) {
        EnergyReadingDto data = energyDataService.getFloorElecRangeData(floor, start, end, datetimeType);
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    @GetMapping("/api/energy/gas")
    public ResponseEntity<EnergyReadingDto> getGasData(String start, String end, byte datetimeType) {
        EnergyReadingDto data = energyDataService.getGasRangeData(start, end, datetimeType);
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    @GetMapping("/api/energy/water")
    public ResponseEntity<EnergyReadingDto> getWaterData(String start, String end, byte datetimeType) {
        EnergyReadingDto data = energyDataService.getWaterRangeData(start, end, datetimeType);
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    @GetMapping("/api/energy/water/{floor}")
    public ResponseEntity<EnergyReadingDto> getWaterData(@PathVariable String floor, String start, String end, byte datetimeType) {
        EnergyReadingDto data = energyDataService.getFloorWaterRangeData(floor, start, end, datetimeType);
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    @GetMapping("/api/energy/carbon")
    public ResponseEntity<List<EnergyReadingDto>> getCarbonData(String start, String end, byte datetimeType) {
        List<EnergyReadingDto> data = energyDataService.getCarbonRangeData(start, end, datetimeType);
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    @GetMapping("/api/energy/bill")
    public ResponseEntity<List<EnergyReadingDto>> getBillData(String start, String end, byte datetimeType) {
        List<EnergyReadingDto> data = energyDataService.getBillRangeData(start, end, datetimeType);
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    @GetMapping("/api/energy/bill/elec/{floor}")
    public ResponseEntity<List<EnergyReadingDto>> getFloorElecBillData(@PathVariable String floor, String start, String end, byte datetimeType) {
        List<EnergyReadingDto> data = energyDataService.getFloorElecBillData(floor, start, end, datetimeType);
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    @GetMapping("/api/energy/bill/water/{floor}")
    public ResponseEntity<List<EnergyReadingDto>> getFloorWaterBillData(@PathVariable String floor, String start, String end, byte datetimeType) {
        List<EnergyReadingDto> data = energyDataService.getFloorWaterBillData(floor, start, end, datetimeType);
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }
}
