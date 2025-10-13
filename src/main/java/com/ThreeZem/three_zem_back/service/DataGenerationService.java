package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.data.constant.PowerConsumptionConst;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.BuildingConfigDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.DeviceConfigDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.FloorConfigDto;
import com.ThreeZem.three_zem_back.data.entity.*;
import com.ThreeZem.three_zem_back.data.enums.DeviceStatus;
import com.ThreeZem.three_zem_back.data.enums.DeviceType;
import com.ThreeZem.three_zem_back.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataGenerationService {

    //<editor-fold desc="Repositories">
    private final BuildingRepository buildingRepository;
    private final DeviceRepository deviceRepository;
    private final FloorRepository floorRepository;
    private final ElectricityReadingRepository electricityReadingRepository;
    private final WaterReadingRepository waterReadingRepository;
    private final GasReadingRepository gasReadingRepository;
    private final SimulationLogicService simulationLogicService;
    private final ApplicationStateService applicationStateService;
    //</editor-fold>


    private volatile Map<Long, Float> electricityUsageBuffer = new ConcurrentHashMap<>();
    private volatile Map<Long, Float> waterUsageBuffer = new ConcurrentHashMap<>();
    private volatile Map<UUID, Float> gasUsageBuffer = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = 10000)
    public void accumulateUsageData() {
        if (!applicationStateService.isHistoricalDataGenerated()) return; // 과거 데이터 생성 완료 전까지 실행 안함

        LocalDateTime now = LocalDateTime.now();
        log.info("---------- 10초 간격 데이터 생성 및 누적 시작 ({}) ----------", now);

        List<Building> buildings = buildingRepository.findAll();
        if (buildings.isEmpty()) {
            log.warn("데이터베이스에 빌딩 정보가 없어 데이터 생성을 건너뜁니다.");
            return;
        }

        Building building = buildings.get(0);
        BuildingConfigDto buildingConfigDto = mapBuildingEntityToConfigDto(building);

        accumulateElectricityUsage(buildingConfigDto);
        accumulateWaterUsage(buildingConfigDto);
        accumulateGasUsage(buildingConfigDto);
    }

    @Scheduled(fixedRate = 300000)  // 5분
    @Transactional
    public void saveAggregatedData() {
        if (!applicationStateService.isHistoricalDataGenerated()) return; // 과거 데이터 생성 완료 전까지 실행 안함

        log.info("---------- 5분 간격 누적 데이터 DB 저장 시작 ----------");
        LocalDateTime now = LocalDateTime.now();

        // 버퍼를 교체하여 누적 중인 데이터에 영향을 주지 않도록 함
        Map<Long, Float> elecDataToSave = electricityUsageBuffer;
        electricityUsageBuffer = new ConcurrentHashMap<>();

        Map<Long, Float> waterDataToSave = waterUsageBuffer;
        waterUsageBuffer = new ConcurrentHashMap<>();

        Map<UUID, Float> gasDataToSave = gasUsageBuffer;
        gasUsageBuffer = new ConcurrentHashMap<>();

        // 전기 데이터 저장
        if (!elecDataToSave.isEmpty()) {
            List<ElectricityReading> readingsToSave = new ArrayList<>();
            elecDataToSave.forEach((deviceId, totalUsage) -> {
                deviceRepository.findById(deviceId).ifPresent(device -> {
                    ElectricityReading reading = new ElectricityReading();
                    reading.setDevice(device);
                    reading.setReadingTime(now);
                    reading.setValue(totalUsage);
                    readingsToSave.add(reading);
                });
            });
            electricityReadingRepository.saveAll(readingsToSave);
            log.info("전력 사용량 데이터 {}건 저장 성공", readingsToSave.size());
        }

        // 수도 데이터 저장
        if (!waterDataToSave.isEmpty()) {
            List<WaterReading> readingsToSave = new ArrayList<>();
            waterDataToSave.forEach((floorId, totalUsage) -> {
                floorRepository.findById(floorId).ifPresent(floor -> {
                    WaterReading reading = new WaterReading();
                    reading.setFloor(floor);
                    reading.setReadingTime(now);
                    reading.setValue(totalUsage);
                    readingsToSave.add(reading);
                });
            });
            waterReadingRepository.saveAll(readingsToSave);
            log.info("수도 사용량 데이터 {}건 저장 성공", readingsToSave.size());
        }

        // 가스 데이터 저장
        if (!gasDataToSave.isEmpty()) {
            List<GasReading> readingsToSave = new ArrayList<>();
            gasDataToSave.forEach((buildingId, totalUsage) -> {
                buildingRepository.findById(buildingId).ifPresent(building -> {
                    GasReading reading = new GasReading();
                    reading.setBuilding(building);
                    reading.setReadingTime(now);
                    reading.setValue(totalUsage);
                    readingsToSave.add(reading);
                });
            });
            gasReadingRepository.saveAll(readingsToSave);
            log.info("가스 사용량 데이터 {}건 저장 성공", readingsToSave.size());
        }
        log.info("-----------------------------------------------------");
    }

    private void accumulateElectricityUsage(BuildingConfigDto buildingConfig) {
        float timeIntervalHours = 10.0f / 3600.0f;
        int generatedCount = 0;
        for (FloorConfigDto floor : buildingConfig.getFloors()) {
            for (DeviceConfigDto device : floor.getDevices()) {
                if (device.getStatus() == DeviceStatus.DEVICE_ON.getValue()) {
                    float powerKw = PowerConsumptionConst.getPowerConsumption(device.getDeviceType());
                    float usage = simulationLogicService.applyNoise(powerKw * timeIntervalHours);
                    electricityUsageBuffer.merge(device.getId(), usage, Float::sum);
                    generatedCount++;
                }
            }
        }
        log.info("누적된 전력 데이터: {}건", generatedCount);
    }

    private void accumulateWaterUsage(BuildingConfigDto buildingConfig) {
        float timeIntervalHours = 10.0f / 3600.0f;
        Map<Integer, Integer> peoplePerFloor = Map.of(1, 7, 2, 30, 3, 30, 4, 15);

        for (FloorConfigDto floor : buildingConfig.getFloors()) {
            int people = peoplePerFloor.getOrDefault(floor.getFloorNum(), 0);
            float usage = simulationLogicService.applyNoise(people * PowerConsumptionConst.WATER_PER_PERSON * timeIntervalHours);
            waterUsageBuffer.merge(floor.getId(), usage, Float::sum);
        }
        log.info("누적된 수도 데이터: {}건", buildingConfig.getFloors().size());
    }

    private void accumulateGasUsage(BuildingConfigDto buildingConfig) {
        float timeIntervalHours = 10.0f / 3600.0f;
        int totalPeople = 82; // README 기반 총 인원

        float usage = simulationLogicService.applyNoise(totalPeople * PowerConsumptionConst.GAS_PER_PERSON * timeIntervalHours);
        gasUsageBuffer.merge(buildingConfig.getId(), usage, Float::sum);
        log.info("누적된 가스 데이터: 1건");
    }

    private float applyNoise(float value) {
        return simulationLogicService.applyNoise(value);
    }

    private BuildingConfigDto mapBuildingEntityToConfigDto(Building building) {
        BuildingConfigDto buildingDto = new BuildingConfigDto();
        buildingDto.setId(building.getId());
        buildingDto.setBuildingName(building.getBuildingName());

        List<Floor> floors = floorRepository.findByBuilding(building);
        List<Device> devices = deviceRepository.findByFloorBuilding(building);

        Map<Long, List<Device>> devicesByFloorId = devices.stream()
                .collect(Collectors.groupingBy(device -> device.getFloor().getId()));

        List<FloorConfigDto> floorDtos = floors.stream().map(floor -> {
            FloorConfigDto floorDto = new FloorConfigDto();
            floorDto.setId(floor.getId());
            floorDto.setFloorNum(floor.getFloorNum());

            List<Device> floorDevices = devicesByFloorId.getOrDefault(floor.getId(), new ArrayList<>());
            List<DeviceConfigDto> deviceDtos = floorDevices.stream().map(device -> {
                DeviceConfigDto deviceDto = new DeviceConfigDto();
                deviceDto.setId(device.getId());
                deviceDto.setDeviceName(device.getDeviceName());
                deviceDto.setDeviceType(DeviceType.fromByte(device.getDeviceType()).name());
                deviceDto.setStatus(device.getStatus());
                return deviceDto;
            }).collect(Collectors.toList());

            floorDto.setDevices(deviceDtos);
            return floorDto;
        }).collect(Collectors.toList());

        buildingDto.setFloors(floorDtos);
        return buildingDto;
    }
}