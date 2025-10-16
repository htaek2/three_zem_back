package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.data.constant.ResponseMessage;
import com.ThreeZem.three_zem_back.data.dto.building.BuildingDto;
import com.ThreeZem.three_zem_back.data.dto.building.DeviceDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.BuildingConfigDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.DeviceConfigDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.FloorConfigDto;
import com.ThreeZem.three_zem_back.data.entity.Building;
import com.ThreeZem.three_zem_back.data.entity.Device;
import com.ThreeZem.three_zem_back.data.entity.Floor;
import com.ThreeZem.three_zem_back.repository.BuildingRepository;
import com.ThreeZem.three_zem_back.repository.DeviceRepository;
import com.ThreeZem.three_zem_back.repository.FloorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final DeviceRepository deviceRepository;

    /// 빌딩 데이터를 가져온다
    public ResponseEntity<List<BuildingDto>> getBuildings() {

        List<Building> result = buildingRepository.findAll();

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        List<BuildingDto> buildings = new ArrayList<>();

        for (Building building : result) {
            buildings.add(new BuildingDto(building.getId(), building.getBuildingName(), building.getAddress(), building.getTotalArea()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(buildings);
    }

    /// 모든 장비 데이터를 가져온다
    public ResponseEntity<List<DeviceDto>> getDevices() {
        List<Device> result = deviceRepository.findAll();

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        List<DeviceDto> devices = new ArrayList<>();

        for (Device device : result) {
            devices.add(device.toDto());
        }

        return ResponseEntity.status(HttpStatus.OK).body(devices);
    }

    /// 층 장비 데이터를 가져온다
    public ResponseEntity<List<DeviceDto>> getDevicesByFloor(int floorNum) {
        List<Device> result = deviceRepository.findByFloorFloorNum(floorNum);

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        List<DeviceDto> devices = new ArrayList<>();

        for (Device device : result) {
            devices.add(device.toDto());
        }

        return ResponseEntity.status(HttpStatus.OK).body(devices);
    }

    public ResponseEntity<DeviceDto> getDevice(Long id) {
        Optional<Device> result = deviceRepository.findById(id);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(result.get().toDto());
    }

    public ResponseEntity<String> updateDevice(Long id, byte status) {
        Optional<Device> result = deviceRepository.findById(id);

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Device device = result.get();
        device.setStatus(status);
        deviceRepository.save(device);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseMessage.SUCCESS);
    }
}