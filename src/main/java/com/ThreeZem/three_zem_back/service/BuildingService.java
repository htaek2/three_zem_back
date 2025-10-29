package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.data.dto.building.BuildingDto;
import com.ThreeZem.three_zem_back.data.dto.building.DeviceDto;
import com.ThreeZem.three_zem_back.data.dto.building.DeviceUpdateResponseDto;
import com.ThreeZem.three_zem_back.data.entity.Building;
import com.ThreeZem.three_zem_back.data.entity.Device;
import com.ThreeZem.three_zem_back.repository.BuildingRepository;
import com.ThreeZem.three_zem_back.repository.DeviceRepository;
import com.ThreeZem.three_zem_back.repository.FloorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

        List<DeviceDto> res = getDevicesMethod();

        if (res.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    public List<DeviceDto> getDevicesMethod () {
        List<Device> result = deviceRepository.findAll();

        if (!result.isEmpty()) {
            List<DeviceDto> devices = new ArrayList<>();

            for (Device device : result) {
                devices.add(device.toDto());
            }

            return devices;
        }
        else {
            return null;
        }
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

    public ResponseEntity<DeviceUpdateResponseDto> updateDevice(Long id, byte status) {
        System.out.print("클라이언트의 요청이 왔습니다." + "id : " + id + "status : " + status);
        Optional<Device> result = deviceRepository.findById(id);

        if (result.isEmpty()) {
            System.out.println("에러가 발생하였습니다.!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }


        Device device = result.get();
        device.setStatus(status);
        deviceRepository.save(device);

        DeviceUpdateResponseDto response = new DeviceUpdateResponseDto(id, status);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}