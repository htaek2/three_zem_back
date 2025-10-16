package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.data.constant.ConfigConst;
import com.ThreeZem.three_zem_back.data.constant.PowerConsum;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.BuildingConfigDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.DeviceConfigDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.FloorConfigDto;
import com.ThreeZem.three_zem_back.data.dto.energy.*;
import com.ThreeZem.three_zem_back.data.entity.*;
import com.ThreeZem.three_zem_back.data.enums.DeviceStatus;
import com.ThreeZem.three_zem_back.data.enums.DeviceType;
import com.ThreeZem.three_zem_back.data.enums.EnergyType;
import com.ThreeZem.three_zem_back.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataGenerationService {

    private final Random random = new Random();

    private final Map<Integer, Integer> peoplePerFloor = new HashMap<>();

    @PostConstruct
    public void init() {
        peoplePerFloor.put(1, 7);
        peoplePerFloor.put(2, 30);
        peoplePerFloor.put(3, 30);
        peoplePerFloor.put(4, 15);
    }

    public float generateElecData(DeviceConfigDto device) {
        float usage = 0.0f;
        float timeIntervalHours = 1f / 3600.0f;
        int dataGenSec = ConfigConst.DATA_UPDATE_MS / 1000;

        if (device.getStatus() == DeviceStatus.DEVICE_ON) {
            float powerKw = PowerConsum.getPowerConsumption(DeviceType.fromByte(device.getDeviceType().getValue()));
            usage = applyNoise(powerKw * timeIntervalHours * dataGenSec);
        }
        return usage;
    }

    public float generateGasData(int totalPeople) {
        float timeIntervalHours = 1f / 3600.0f;
        int dataGenSec = ConfigConst.DATA_UPDATE_MS / 1000;
        float usage = totalPeople * PowerConsum.GAS_PER_PERSON * timeIntervalHours * dataGenSec;
        return applyNoise(usage);
    }

    public float generateWaterData(FloorConfigDto floor) {
        float timeIntervalHours = 1f / 3600.0f;
        int dataGenSec = ConfigConst.DATA_UPDATE_MS / 1000;
        int people = peoplePerFloor.getOrDefault(floor.getFloorNum(), 10);
        float usage = people * PowerConsum.WATER_PER_PERSON * timeIntervalHours * dataGenSec;
        return applyNoise(usage);
    }

    public boolean isDeviceOn(Device device, LocalDateTime now) {
        double probability;
        DeviceType type = DeviceType.fromByte(device.getDeviceType());

        if (isOperatingHours(now)) {
            double timeFactor = isPeakHours(now) ? 0.9 : 0.7;
            switch (type) {
                case COMPUTER, LIGHT -> probability = timeFactor;
                case AIR_CONDITIONER -> probability = getACSeasonalFactor(now.getMonth()) * timeFactor;
                default -> probability = 0.0;
            }
        } else {
            // 영업 외 시간 (야간, 주말 등)
            switch (type) {
                case COMPUTER -> probability = 0.02; // 2% 확률 (대기 전력, 켜놓고 퇴근 등)
                case LIGHT -> probability = 0.01; // 1% 확률 (보안등, 청소 등)
                default -> probability = 0.0; // AC 등은 거의 0
            }
        }
        return random.nextDouble() < probability;
    }

    public boolean isOperatingHours(LocalDateTime now) {
        DayOfWeek day = now.getDayOfWeek();
        int hour = now.getHour();
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY && hour >= 8 && hour < 21;
    }

    public boolean isPeakHours(LocalDateTime now) {
        int hour = now.getHour();
        return hour >= 9 && hour < 18;
    }

    public float getACSeasonalFactor(Month month) {
        return switch (month) {
            case JUNE, OCTOBER -> 0.7f;
            case JULY, AUGUST, SEPTEMBER -> 0.95f;
            default -> 0.05f;
        };
    }

    public float getGasSeasonalFactor(Month month) {
        return switch (month) {
            case NOVEMBER, MARCH -> 2.5f;
            case DECEMBER, JANUARY, FEBRUARY -> 4.0f;
            default -> 0.2f;
        };
    }

    public float getPeopleFactor(LocalDateTime now) {
        if (!isOperatingHours(now)) return 0.05f;
        if (now.getHour() >= 12 && now.getHour() < 13) return 0.4f;
        return 0.9f;
    }

    public float applyNoise(float value) {
        float noise = (random.nextFloat() * 2 - 1) * 0.05f; // -0.05 ~ 0.05
        return value * (1 + noise);
    }
}



//private void generateHistoricalData(int startYearsAgo, int intervalMinutes) {
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