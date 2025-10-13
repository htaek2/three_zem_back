package com.ThreeZem.three_zem_back.controller;

import com.ThreeZem.three_zem_back.data.dto.building.BuildingDto;
import com.ThreeZem.three_zem_back.data.dto.building.DeviceDto;
import com.ThreeZem.three_zem_back.service.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    /// 빌딩 목록을 가져온다
    @GetMapping("/api/buildings")
    public ResponseEntity<List<BuildingDto>> getBuildings() {
        return buildingService.getBuildings();
    }

    /// 모든 장비를 가져온다
    @GetMapping("/api/devices")
    public ResponseEntity<List<DeviceDto>> getDevices() {
        return buildingService.getDevices();
    }

    /// 층 수의 모든 장비를 가져온다
    @GetMapping("/api/devices/{floorNum}")
    public ResponseEntity<List<DeviceDto>> getDevicesByFloor(@PathVariable int floorNum) {
        return buildingService.getDevicesByFloor(floorNum);
    }

    /// 장비 ID로 장비 객체를 가져온다
    @GetMapping("/api/device/{id}")
    public ResponseEntity<DeviceDto> getDevice(@PathVariable Long id) {
        return buildingService.getDevice(id);
    }

    /// 장비의 상태를 수정한다
    @PatchMapping("/api/device/{id}")
    public ResponseEntity<String> updateDevice(@RequestBody Map<String, Byte> payload, @PathVariable Long id) {
        return buildingService.updateDevice(id, payload.get("status"));
    }

}
