package com.ThreeZem.three_zem_back.controller;

import com.ThreeZem.three_zem_back.data.dto.energy.EnergyReadingDto;
import com.ThreeZem.three_zem_back.data.dto.energy.RangeDataRequestDto;
import com.ThreeZem.three_zem_back.service.EnergyDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalTime;

@RestController
@RequiredArgsConstructor
public class EnergyDataController {

    private final EnergyDataService energyDataService;

    @GetMapping("/api/energy/elec")
    public ResponseEntity<EnergyReadingDto> getElecData(@RequestBody RangeDataRequestDto rangeDataRequestDto) {
        return energyDataService.getElecRangeData(rangeDataRequestDto);
    }

    @GetMapping("/api/energy/gas")
    public ResponseEntity<EnergyReadingDto> getGasData(@RequestBody RangeDataRequestDto rangeDataRequestDto) {
        return energyDataService.getGasRangeData(rangeDataRequestDto);
    }

    @GetMapping("/api/energy/water")
    public ResponseEntity<EnergyReadingDto> getWaterData(@RequestBody RangeDataRequestDto rangeDataRequestDto) {
        return energyDataService.getWaterRangeData(rangeDataRequestDto);
    }


}
