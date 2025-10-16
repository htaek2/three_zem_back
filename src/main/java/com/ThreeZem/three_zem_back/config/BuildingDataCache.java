package com.ThreeZem.three_zem_back.config;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class BuildingDataCache {

    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final DeviceRepository deviceRepository;
    private BuildingConfigDto buildingDto;
    private Building buildingEntity;
    private List<Floor> floorEntities;
    private List<Device> deviceEntities;

    public void init(Long memberId) {
        Optional<Building> building = buildingRepository.findByMemberId(memberId);

        if (building.isEmpty()) {
            throw new RuntimeException("[ERROR] 해당 계정과 관련된 빌딩 정보가 없습니다.");
        }
        else {
            buildingEntity = building.get();

            List<Device> devices = deviceRepository.findByFloorBuilding(building.get());
            deviceEntities = devices;

            List<Floor> floors = floorRepository.findByBuilding(building.get());
            floorEntities = floors;

            Map<Long, List<Device>> devicesByFloorId = devices.stream().collect(Collectors.groupingBy(d -> d.getFloor().getId()));
            List<FloorConfigDto> floorDtos = floors.stream().map(floor -> {
                List<Device> floorDevices = devicesByFloorId.getOrDefault(floor.getId(), new ArrayList<>());
                List<DeviceConfigDto> deviceDtos = floorDevices.stream().map(DeviceConfigDto::new).collect(Collectors.toList());
                return new FloorConfigDto(floor, deviceDtos);
            }).collect(Collectors.toList());

            buildingDto = new BuildingConfigDto(building.get(), floorDtos);
        }
    }

    public Building getBuildingEntity() {
        if (buildingEntity == null) {
            log.warn("빌딩 데이터를 가져오기 전에 먼저 유저 ID로 init 메서드를 실행해주세요");
            return null;
        }
        return buildingEntity;
    }

    public List<Floor> getFloorEntities() {
        if (floorEntities == null) {
            log.warn("빌딩 데이터를 가져오기 전에 먼저 유저 ID로 init 메서드를 실행해주세요");
            return null;
        }
        return floorEntities;
    }

    public List<Device> getDeviceEntities() {
        if (deviceEntities == null) {
            log.warn("빌딩 데이터를 가져오기 전에 먼저 유저 ID로 init 메서드를 실행해주세요");
            return null;
        }
        return deviceEntities;
    }

    public BuildingConfigDto getBuildingDto() {
        if (buildingDto == null) {
            log.warn("빌딩 데이터를 가져오기 전에 먼저 유저 ID로 init 메서드를 실행해주세요");
            return null;
        }
        return buildingDto;
    }

    public void clearData() {
        buildingDto = null;
        buildingEntity = null;
        floorEntities = null;
        deviceEntities = null;
    }
}
