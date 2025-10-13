package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.data.constant.ConfigConst;
import com.ThreeZem.three_zem_back.data.constant.PowerConsum;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.BuildingConfigDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.DeviceConfigDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.FloorConfigDto;
import com.ThreeZem.three_zem_back.data.dto.energy.BuildingEnergyDto;
import com.ThreeZem.three_zem_back.data.dto.energy.EnergyReadingsDto;
import com.ThreeZem.three_zem_back.data.dto.energy.ReadingDto;
import com.ThreeZem.three_zem_back.data.entity.*;
import com.ThreeZem.three_zem_back.data.enums.DeviceStatus;
import com.ThreeZem.three_zem_back.data.enums.DeviceType;
import com.ThreeZem.three_zem_back.data.enums.EnergyType;
import com.ThreeZem.three_zem_back.repository.*;
import com.ThreeZem.three_zem_back.util.TimeUtil;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataGenerationService {

    /// 한 번에 데이터 생성, 저장을 처리할 개수
    private static final int BATCH_SIZE = 2000;// 5분

    private final BuildingRepository buildingRepository;
    private final DeviceRepository deviceRepository;
    private final FloorRepository floorRepository;
    private final ElectricityReadingRepository electricityReadingRepository;
    private final WaterReadingRepository waterReadingRepository;
    private final GasReadingRepository gasReadingRepository;
    private final SimulationLogicService simulationLogicService;
    private final ApplicationStateService applicationStateService;

    // 가장 최근 데이터. 실시간 데이터를 원할 때 이거 가지고 가면 됨.
    private volatile Map<Long, Float> recentElecDatas = new HashMap<>();
    private volatile Map<UUID, Float> recentGasDatas = new HashMap<>();
    private volatile Map<Long, Float> recentWaterDatas = new HashMap<>();

    private volatile Map<Long, Float> electricityUsageBuffer = new ConcurrentHashMap<>();
    private volatile Map<Long, Float> waterUsageBuffer = new ConcurrentHashMap<>();
    private volatile Map<UUID, Float> gasUsageBuffer = new ConcurrentHashMap<>();

    Map<Integer, Integer> peoplePerFloor = Map.of(1, 7, 2, 30, 3, 30, 4, 15);

    @PostConstruct
    public void init() {
        System.out.println("ddddddddddddddddddd");
    }

    /// 실시간 빌딩 에너지 데이터 생성
    public BuildingEnergyDto getBuildingEnergyData(UUID buildingId) {
        // recent 데이터를 사용해서 데이터 클래스 만들고 보내기
        BuildingEnergyDto buildingEnergy = new BuildingEnergyDto();

        Optional<Building> building = buildingRepository.findById(buildingId);
        if (building.isEmpty()) {
            log.error("데이터베이스에 빌딩 정보가 없습니다.");
            return null;
        }

        BuildingConfigDto buildingConfig = mapBuildingEntityToDto(building.get());
        
        
        // TODO 데이터베이스에서 빌딩 데이터를 가져오면 안에 에너지 사용량 데이터들도 있음?
        // 그럼 미리 빌딩 구조를 알려주는 용도로 초기화해서 사용하도록
        



        return buildingEnergy;
    }

    /// 생성된 데이터를 누적
    @Scheduled(fixedRate = ConfigConst.DATA_UPDATE_MS)
    public void accumulateUsageData() {
        if (!applicationStateService.getDataGenerated()) return; // 과거 데이터 생성 완료 전까지 실행 안함

        LocalDateTime now = LocalDateTime.now();
        log.info("---------- 10초 간격 데이터 생성 및 누적 시작 ({}) ----------", now);

        // TODO 빌딩을 임의로 가져옴. auth 정보 등으로 가져올 방법 찾기
        List<Building> buildings = buildingRepository.findAll();
        if (buildings.isEmpty()) {
            log.warn("데이터베이스에 빌딩 정보가 없어 데이터 생성을 건너뜁니다.");
            return;
        }

        Building building = buildings.get(0);
        BuildingConfigDto buildingConfigDto = mapBuildingEntityToDto(building);

        accumulateElectricityUsage(buildingConfigDto);
        accumulateWaterUsage(buildingConfigDto);
        accumulateGasUsage(buildingConfigDto);
    }

    /// 전력 데이터 생성
    public float generateElecData(DeviceConfigDto device) {
        float usage = 0.0f;
        float timeIntervalHours = 1f / 3600.0f;  // kWh를 kWs로 바꾸기 위함
        int dataGenSec = DATA_GEN_MS / 1000;

        // 장비가 켜져 있는 경우
        if (device.getStatus() == DeviceStatus.DEVICE_ON) {
            // 사용량 계산
            float powerKw = PowerConsum.getPowerConsumption(DeviceType.fromByte(device.getDeviceType().getValue()));
            usage = simulationLogicService.applyNoise(powerKw * timeIntervalHours * dataGenSec);
        }

        return usage;
    }

    /// 가스 데이터 생성
    public float generateGasData(int totalPeople) {
        float timeIntervalHours = 1f / 3600.0f;
        int dataGenSec = DATA_GEN_MS / 1000;

        float usage = totalPeople * PowerConsum.GAS_PER_PERSON * timeIntervalHours * dataGenSec;
        usage = simulationLogicService.applyNoise(usage);
        // TODO 계절, 시간 팩터에 맞게 랜덤 생성

        return usage;
    }

    /// 수도 데이터 생성
    public float generateWaterData(FloorConfigDto floor) {
        float timeIntervalHours = 1f / 3600.0f;  // kWh를 kWs로 바꾸기 위함
        int dataGenSec = DATA_GEN_MS / 1000;
        int people = peoplePerFloor.getOrDefault(floor.getFloorNum(), 0);

        float usage = people * PowerConsum.WATER_PER_PERSON * timeIntervalHours * dataGenSec;
        usage = simulationLogicService.applyNoise(usage);
        // TODO 계절, 시간 팩터에 맞게 랜덤 생성

        return usage;
    }

    /// 전력 사용량 생성 후 누적
    private void accumulateElectricityUsage(BuildingConfigDto buildingConfig) {
        int generatedCount = 0;

        for (FloorConfigDto floor : buildingConfig.getFloors()) {
            for (DeviceConfigDto device : floor.getDevices()) {
                float usage = generateElecData(device);
                Long deviceId = device.getId();

                recentElecDatas.put(deviceId, usage);
                recentElecDatas = new HashMap<>();

                electricityUsageBuffer.merge(deviceId, usage, Float::sum);
                generatedCount++;
            }
        }

        log.info("누적된 전력 데이터: {}건", generatedCount);
    }

    /// 가스 사용량 생성 후 누적
    private void accumulateGasUsage(BuildingConfigDto buildingConfig) {
        int totalPeople = 82; // README 기반 총 인원

        float usage = generateGasData(totalPeople);
        UUID buildingId = buildingConfig.getId();

        recentGasDatas.put(buildingId, usage);
        recentGasDatas = new HashMap<>();

        gasUsageBuffer.merge(buildingId, usage, Float::sum);

        log.info("누적된 가스 데이터: 1건");
    }

    /// 수도 사용량 생성 후 누적
    private void accumulateWaterUsage(BuildingConfigDto buildingConfig) {
        int generatedCount = 0;

        for (FloorConfigDto floor : buildingConfig.getFloors()) {
            float usage = generateWaterData(floor);
            Long floorId = floor.getId();

            recentWaterDatas.put(floorId, usage);
            recentWaterDatas = new HashMap<>();

            waterUsageBuffer.merge(floorId, usage, Float::sum);
            generatedCount++;
        }

        log.info("누적된 수도 데이터: {}건", generatedCount);
    }

    /// 누적된 데이터를 주기적으로 데이터베이스에 저장
    @Transactional
    @Scheduled(fixedRate = ConfigConst.DATA_UPDATE_MS)
    public void saveAggregatedData() {
        if (!applicationStateService.getDataGenerated()) return; // 과거 데이터 생성 완료 전까지 실행 안함

        log.info("---------- 누적 데이터 DB 저장 ----------");
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
                    readingsToSave.add(new ElectricityReading(device, now, totalUsage));
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
                    readingsToSave.add(new WaterReading(floor, now, totalUsage));
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
                    readingsToSave.add(new GasReading(building, now, totalUsage));
                });
            });
            gasReadingRepository.saveAll(readingsToSave);
            log.info("가스 사용량 데이터 {}건 저장 성공", readingsToSave.size());
        }
        log.info("-----------------------------------------------------");
    }

    /// 빌딩 Entity를 빌딩 Dto로 변환
    private BuildingConfigDto mapBuildingEntityToDto(Building building) {

        // DB에서 빌딩에 속한 장비와 층 데이터를 불러옴
        List<Device> devices = deviceRepository.findByFloorBuilding(building);
        List<Floor> floors = floorRepository.findByBuilding(building);

        // 층 Entity를 Dto로 맵핑
        Map<Long, List<Device>> devicesByFloorId = devices.stream().collect(Collectors.groupingBy(d -> d.getFloor().getId()));
        List<FloorConfigDto> floorDtos = floors.stream().map(floor -> {

            // 층별 전력 장비 Entity를 Dto로 맵핑
            List<Device> floorDevices = devicesByFloorId.getOrDefault(floor.getId(), new ArrayList<>());
            List<DeviceConfigDto> deviceDtos = floorDevices.stream().map(DeviceConfigDto::new).collect(Collectors.toList());



            return new FloorConfigDto(floor, deviceDtos);
        }).collect(Collectors.toList());

        return new BuildingConfigDto(building, floorDtos);
    }

//    private void generateHistoricalData(int startYearsAgo, int intervalMinutes) {
//        log.info("========== 과거 {}년치 데이터 생성을 시작합니다. ({}분 단위) ==========", startYearsAgo, intervalMinutes);
//
//        Building building = buildingRepository.findAll().get(0);
//        List<Floor> floors = floorRepository.findByBuilding(building);
//        List<Device> devices = deviceRepository.findByFloorBuilding(building);
//
//        List<ElectricityReading> elecBuffer = new ArrayList<>();
//        List<WaterReading> waterBuffer = new ArrayList<>();
//        List<GasReading> gasBuffer = new ArrayList<>();
//
//        LocalDateTime cursor = LocalDateTime.now().minusYears(startYearsAgo);
//        LocalDateTime endTime = LocalDateTime.now();
//        YearMonth currentMonth = YearMonth.from(cursor);
//
//        log.info("{} 데이터 생성 중...", currentMonth);
//
//        while (cursor.isBefore(endTime)) {
//            if (!YearMonth.from(cursor).equals(currentMonth)) {
//                currentMonth = YearMonth.from(cursor);
//                log.info("{} 데이터 생성 중...", currentMonth);
//            }
//
//            float intervalHours = (float) intervalMinutes / 60.0f;
//
//            for (Device device : devices) {
//                if (simulationLogicService.isDeviceOn(device, cursor)) {
//                    float usage = simulationLogicService.applyNoise(PowerConsum.getPowerConsumption(DeviceType.fromByte(device.getDeviceType()).name()) * intervalHours);
//                    ElectricityReading reading = new ElectricityReading();
//                    reading.setDevice(device);
//                    reading.setReadingTime(cursor);
//                    reading.setValue(usage);
//                    elecBuffer.add(reading);
//                }
//            }
//
//            Map<Integer, Integer> peoplePerFloor = Map.of(1, 7, 2, 30, 3, 30, 4, 15);
//            for (Floor floor : floors) {
//                int people = (int) (peoplePerFloor.getOrDefault(floor.getFloorNum(), 0) * simulationLogicService.getPeopleFactor(cursor));
//                float usage = simulationLogicService.applyNoise(people * PowerConsum.WATER_PER_PERSON * intervalHours);
//                WaterReading reading = new WaterReading();
//                reading.setFloor(floor);
//                reading.setReadingTime(cursor);
//                reading.setValue(usage);
//                waterBuffer.add(reading);
//            }
//
//            int totalPeople = (int) (82 * simulationLogicService.getPeopleFactor(cursor));
//            float gasUsage = simulationLogicService.applyNoise(totalPeople * PowerConsum.GAS_PER_PERSON * intervalHours * simulationLogicService.getGasSeasonalFactor(cursor.getMonth()));
//            GasReading gasReading = new GasReading();
//            gasReading.setBuilding(building);
//            gasReading.setReadingTime(cursor);
//            gasReading.setValue(gasUsage);
//            gasBuffer.add(gasReading);
//
//            if (elecBuffer.size() >= BATCH_SIZE) {
//                electricityReadingRepository.saveAll(elecBuffer);
//                elecBuffer.clear();
//            }
//            if (waterBuffer.size() >= BATCH_SIZE) {
//                waterReadingRepository.saveAll(waterBuffer);
//                waterBuffer.clear();
//            }
//            if (gasBuffer.size() >= BATCH_SIZE) {
//                gasReadingRepository.saveAll(gasBuffer);
//                gasBuffer.clear();
//            }
//
//            cursor = cursor.plusMinutes(intervalMinutes);
//        }
//
//        if (!elecBuffer.isEmpty()) electricityReadingRepository.saveAll(elecBuffer);
//        if (!waterBuffer.isEmpty()) waterReadingRepository.saveAll(waterBuffer);
//        if (!gasBuffer.isEmpty()) gasReadingRepository.saveAll(gasBuffer);
//
//        log.info("========== 과거 데이터 생성을 완료했습니다. ==========");
//    }
}