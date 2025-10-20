package com.ThreeZem.three_zem_back.config;

import com.ThreeZem.three_zem_back.data.dto.buildingConfig.BuildingConfigDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.DeviceConfigDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.FloorConfigDto;
import com.ThreeZem.three_zem_back.data.entity.Building;
import com.ThreeZem.three_zem_back.data.entity.Device;
import com.ThreeZem.three_zem_back.data.entity.Floor;
import com.ThreeZem.three_zem_back.data.enums.DeviceStatus;
import com.ThreeZem.three_zem_back.repository.BuildingRepository;
import com.ThreeZem.three_zem_back.repository.DeviceRepository;
import com.ThreeZem.three_zem_back.repository.FloorRepository;
import jakarta.annotation.PostConstruct;
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
    /// 층별 사용인원
    private final Map<Integer, Integer> peoplePerFloor = new HashMap<>();

    public void init() {
        // 사용인원 임시로 정의
        peoplePerFloor.put(1, 7);
        peoplePerFloor.put(2, 30);
        peoplePerFloor.put(3, 30);
        peoplePerFloor.put(4, 15);

        List<Building> building = buildingRepository.findAll();

        if (building.isEmpty()) {
            throw new RuntimeException("[ERROR] 빌딩 정보가 없습니다.");
        }
        else {
            buildingEntity = building.get(0);

            List<Device> devices = deviceRepository.findByFloorBuilding(buildingEntity);
            deviceEntities = devices;

            List<Floor> floors = floorRepository.findByBuilding(buildingEntity);
            floorEntities = floors;

            Map<Long, List<Device>> devicesByFloorId = devices.stream().collect(Collectors.groupingBy(d -> d.getFloor().getId()));
            List<FloorConfigDto> floorDtos = floors.stream().map(floor -> {
                List<Device> floorDevices = devicesByFloorId.getOrDefault(floor.getId(), new ArrayList<>());
                floorDevices.forEach(device -> {device.setStatus(DeviceStatus.DEVICE_ON.getValue());});  // 모든 장비 다 켜
                List<DeviceConfigDto> deviceDtos = floorDevices.stream().map(DeviceConfigDto::new).collect(Collectors.toList());
                return new FloorConfigDto(floor, deviceDtos);
            }).collect(Collectors.toList());

            buildingDto = new BuildingConfigDto(buildingEntity, floorDtos);
        }
    }

    public int getPeoplePerFloor(int floorNum) {
        return  peoplePerFloor.getOrDefault(floorNum, 10);
    }

    public int getTotalPeople() {
        return 82;
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
}
