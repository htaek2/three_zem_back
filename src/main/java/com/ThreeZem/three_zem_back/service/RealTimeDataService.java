package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.config.BuildingDataCache;
import com.ThreeZem.three_zem_back.data.constant.ConfigConst;
import com.ThreeZem.three_zem_back.data.dto.WeatherDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.BuildingConfigDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.DeviceConfigDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.FloorConfigDto;
import com.ThreeZem.three_zem_back.data.dto.energy.*;
import com.ThreeZem.three_zem_back.data.entity.*;
import com.ThreeZem.three_zem_back.data.enums.DeviceType;
import com.ThreeZem.three_zem_back.data.enums.EnergyType;
import com.ThreeZem.three_zem_back.repository.*;
import com.ThreeZem.three_zem_back.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealTimeDataService {

    private final ElectricityReadingRepository electricityReadingRepository;
    private final WaterReadingRepository waterReadingRepository;
    private final GasReadingRepository gasReadingRepository;
    private final BuildingRepository buildingRepository;
    private final DeviceRepository deviceRepository;
    private final FloorRepository floorRepository;
    private final ApplicationStateService applicationStateService;
    private final DataGenerationService dataGenerationService;
    private final BuildingDataCache  buildingDataCache;

    private volatile Map<Long, Float> recentElecDatas = new ConcurrentHashMap<>();
    private volatile Map<UUID, Float> recentGasDatas = new ConcurrentHashMap<>();
    private volatile Map<Long, Float> recentWaterDatas = new ConcurrentHashMap<>();

    private volatile Map<Long, Float> electricityUsageBuffer = new ConcurrentHashMap<>();
    private volatile Map<Long, Float> waterUsageBuffer = new ConcurrentHashMap<>();
    private volatile Map<UUID, Float> gasUsageBuffer = new ConcurrentHashMap<>();

    /// 실시간 데이터 생성 및 누적
    @Scheduled(fixedRate = ConfigConst.DATA_UPDATE_MS)
    public void accumulateUsageData() {

        // 과거 데이터 생성 중이라면 빠꾸
        if (!applicationStateService.getDataGenerated()) {
            return;
        }

        // 빌딩 데이터를 등록하지 않았다면(로그인 하지 않고 실행) 빠꾸
        if (!applicationStateService.getIsLogin()) {
            return;
        }

        log.info("---------- 10초 간격 데이터 생성 ({}) ----------", LocalDateTime.now().format(TimeUtil.getDateTimeFormatter()));

        accumulateElectricityUsage(buildingDataCache.getBuildingDto());
        accumulateWaterUsage(buildingDataCache.getBuildingDto());
        accumulateGasUsage(buildingDataCache.getBuildingDto());
    }

    // 실시간 데이터 저장
    @Transactional
    @Scheduled(fixedRate = ConfigConst.DATA_UPDATE_MS)
    public void saveAggregatedData() {

        // 과거 데이터 생성 중이라면 빠꾸
        if (!applicationStateService.getDataGenerated()) {
            return;
        }

        // 빌딩 데이터를 등록하지 않았다면(로그인 하지 않고 실행) 빠꾸
        if (!applicationStateService.getIsLogin()) {
            return;
        }

        log.info("---------- 누적 데이터 DB 저장 ----------");
        LocalDateTime now = LocalDateTime.now();

        // 누적 데이터를 다른 컨테이너에 담고 누적 컨테이너는 초기화
        Map<Long, Float> elecDataToSave = electricityUsageBuffer;
        log.info("---- 전력 데이터 {}", elecDataToSave.size());

        electricityUsageBuffer = new ConcurrentHashMap<>();

        Map<Long, Float> waterDataToSave = waterUsageBuffer;
        log.info("---- 수도 데이터 {}", waterDataToSave.size());

        waterUsageBuffer = new ConcurrentHashMap<>();

        Map<UUID, Float> gasDataToSave = gasUsageBuffer;
        log.info("---- 가스 데이터 {}", gasDataToSave.size());

        gasUsageBuffer = new ConcurrentHashMap<>();


        // 누적 데이터를 각 DB에 저장
        if (!elecDataToSave.isEmpty()) {
            List<ElectricityReading> readingsToSave = new ArrayList<>();
            elecDataToSave.forEach((deviceId, totalUsage) -> {
                deviceRepository.findById(deviceId).ifPresent(device -> {
                    readingsToSave.add(new ElectricityReading(device, now, totalUsage));
                });
            });
            electricityReadingRepository.saveAll(readingsToSave);
        }

        if (!waterDataToSave.isEmpty()) {
            List<WaterReading> readingsToSave = new ArrayList<>();
            waterDataToSave.forEach((floorId, totalUsage) -> {
                floorRepository.findById(floorId).ifPresent(floor -> {
                    readingsToSave.add(new WaterReading(floor, now, totalUsage));
                });
            });
            waterReadingRepository.saveAll(readingsToSave);
        }

        if (!gasDataToSave.isEmpty()) {
            List<GasReading> readingsToSave = new ArrayList<>();
            gasDataToSave.forEach((buildingId, totalUsage) -> {
                buildingRepository.findById(buildingId).ifPresent(building -> {
                    readingsToSave.add(new GasReading(building, now, totalUsage));
                });
            });
            gasReadingRepository.saveAll(readingsToSave);
        }
    }

    public BuildingEnergyDto getBuildingEnergyData(UUID buildingId) {

        // 과거 데이터 생성 중이라면 빠꾸
        if (!applicationStateService.getDataGenerated()) {
            log.warn("[WARN] 과거 데이터 생성 중");
            return null;
        }

        // 빌딩 데이터를 등록하지 않았다면(로그인 하지 않고 실행) 빠꾸
        if (!applicationStateService.getIsLogin()) {
            log.warn("[WARN] 빌딩 데이터 필요");
            return null;
        }


        Building building = buildingDataCache.getBuildingEntity();
        List<Floor> floors = buildingDataCache.getFloorEntities();
        List<Device> devices = buildingDataCache.getDeviceEntities();

        BuildingEnergyDto buildingEnergyDto = new BuildingEnergyDto();

        // 가스 사용량 설정
        EnergyReadingDto gasUsage = new EnergyReadingDto();
        gasUsage.setEnergyType(EnergyType.GAS);
        gasUsage.setDatas(Collections.singletonList(new ReadingDto(LocalDateTime.now(), recentGasDatas.getOrDefault(buildingId, 0f))));
        buildingEnergyDto.setGasUsage(gasUsage);

        Map<Long, List<Device>> devicesByFloorId = devices.stream().collect(Collectors.groupingBy(d -> d.getFloor().getId()));

        List<FloorEnergyDto> floorEnergyDtos = floors.stream().map(floor -> {
            FloorEnergyDto floorEnergyDto = new FloorEnergyDto();
            floorEnergyDto.setFloorNum(floor.getFloorNum());

            // 층별 수도 사용량 설정
            EnergyReadingDto waterUsage = new EnergyReadingDto();
            waterUsage.setEnergyType(EnergyType.WATER);
            waterUsage.setDatas(Collections.singletonList(new ReadingDto(LocalDateTime.now(), recentWaterDatas.getOrDefault(floor.getId(), 0f))));
            floorEnergyDto.setWaterUsage(waterUsage);

            // 장비별 전력 사용량 설정
            List<Device> floorDevices = devicesByFloorId.getOrDefault(floor.getId(), Collections.emptyList());
            List<DeviceEnergyDto> deviceEnergyDtos = floorDevices.stream().map(device -> {
                DeviceEnergyDto deviceEnergyDto = new DeviceEnergyDto();
                deviceEnergyDto.setDeviceId(device.getId());
                deviceEnergyDto.setDeviceName(device.getDeviceName());
                deviceEnergyDto.setDeviceType(DeviceType.fromByte(device.getDeviceType()));

                EnergyReadingDto elecUsage = new EnergyReadingDto();
                elecUsage.setEnergyType(EnergyType.ELECTRICITY);

                elecUsage.setDatas(Collections.singletonList(new ReadingDto(LocalDateTime.now(), recentElecDatas.getOrDefault(device.getId(), 0f))));

                deviceEnergyDto.setElectricityUsage(elecUsage);

                return deviceEnergyDto;
            }).collect(Collectors.toList());

            floorEnergyDto.setDevices(deviceEnergyDtos);
            return floorEnergyDto;
        }).collect(Collectors.toList());

        buildingEnergyDto.setFloors(floorEnergyDtos);

        recentGasDatas.clear();
        recentWaterDatas.clear();
        recentElecDatas.clear();

        return buildingEnergyDto;
    }

    private void accumulateElectricityUsage(BuildingConfigDto buildingConfig) {
        for (FloorConfigDto floor : buildingConfig.getFloors()) {
            for (DeviceConfigDto device : floor.getDevices()) {
                float usage = dataGenerationService.generateElecData(device);
                Long deviceId = device.getId();
                recentElecDatas.put(deviceId, usage);
                electricityUsageBuffer.merge(deviceId, usage, Float::sum);
            }
        }
    }

    private void accumulateGasUsage(BuildingConfigDto buildingConfig) {
        int totalPeople = 82;
        float usage = dataGenerationService.generateGasData(totalPeople);
        UUID buildingId = buildingConfig.getId();
        recentGasDatas.put(buildingId, usage);
        gasUsageBuffer.merge(buildingId, usage, Float::sum);
    }

    private void accumulateWaterUsage(BuildingConfigDto buildingConfig) {
        for (FloorConfigDto floor : buildingConfig.getFloors()) {
            float usage = dataGenerationService.generateWaterData(floor);
            Long floorId = floor.getId();
            recentWaterDatas.put(floorId, usage);
            waterUsageBuffer.merge(floorId, usage, Float::sum);
        }
    }

}
