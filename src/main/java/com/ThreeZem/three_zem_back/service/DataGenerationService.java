package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.config.BuildingDataCache;
import com.ThreeZem.three_zem_back.data.constant.ConfigConst;
import com.ThreeZem.three_zem_back.data.constant.PowerConsum;
import com.ThreeZem.three_zem_back.data.dto.building.BuildingDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.BuildingConfigDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.DeviceConfigDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.FloorConfigDto;
import com.ThreeZem.three_zem_back.data.dto.energy.*;
import com.ThreeZem.three_zem_back.data.entity.*;
import com.ThreeZem.three_zem_back.data.enums.DeviceStatus;
import com.ThreeZem.three_zem_back.data.enums.DeviceType;
import com.ThreeZem.three_zem_back.data.enums.EnergyType;
import com.ThreeZem.three_zem_back.repository.*;
import com.ThreeZem.three_zem_back.util.TimeUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataGenerationService {

    private final BuildingDataCache buildingDataCache;
    private final ElectricityReadingRepository electricityReadingRepository;
    private final GasReadingRepository gasReadingRepository;
    private final WaterReadingRepository waterReadingRepository;

    private final Random random = new Random();

    public float generateElecData(DeviceType type, DeviceStatus status, boolean isDataCreate) {
        float usage = 0.0f;
        float timeIntervalHours = 1f / 3600.0f;
        int dataGenSec = ConfigConst.DATA_UPDATE_MS / 1000;

        if (status == DeviceStatus.DEVICE_ON || isDataCreate) {
            float powerKw = PowerConsum.getPowerConsumption(type);
            usage = applyNoise(powerKw * timeIntervalHours * dataGenSec);
        }
        return usage;
    }

    public float generateGasData(int totalNum, LocalDateTime now) {
        float timeIntervalHours = 1f / 3600.0f;
        int dataGenSec = ConfigConst.DATA_UPDATE_MS / 1000;
        int people = (int) (buildingDataCache.getTotalPeople() * getPeopleFactor(now));
        float usage = people * PowerConsum.GAS_PER_PERSON * timeIntervalHours * dataGenSec;
        return applyNoise(usage);
    }

    public float generateWaterData(int floorNum, LocalDateTime now) {
        float timeIntervalHours = 1f / 3600.0f;  // 1시간당 사용량을 1초당 사용 전력량으로 변환하기 위한 값
        int dataGenSec = ConfigConst.DATA_UPDATE_MS / 1000;  // 데이터 생성 주기를 ms에서 sec로 변환
        int people = (int) (buildingDataCache.getPeoplePerFloor(floorNum) * getPeopleFactor(now));  // 사용인원수

        float usage = people * PowerConsum.WATER_PER_PERSON * timeIntervalHours * dataGenSec;

        return applyNoise(usage);
    }

    /// 실시간 데이터 생성 외의 데이터 생성 시 장비가 켜져 있는지 시뮬레이션
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

    /// 과거 데이터 생성
    public void generateHistoricalData(int startYearsAgo, int intervalMinutes) {

        String now = LocalDateTime.now().format(TimeUtil.getDateTimeFormatter());
        log.info("[DATA] {} ----- 과거 {}년치 데이터 생성을 시작합니다. ({}분 단위)", now, startYearsAgo, intervalMinutes);

        Building building = buildingDataCache.getBuildingEntity();
        List<Floor> floors = buildingDataCache.getFloorEntities();
        List<Device> devices = buildingDataCache.getDeviceEntities();

        List<ElectricityReading> elecBuffer = new ArrayList<>();
        List<WaterReading> waterBuffer = new ArrayList<>();
        List<GasReading> gasBuffer = new ArrayList<>();

        LocalDateTime cursor = LocalDateTime.now().minusYears(startYearsAgo);
        LocalDateTime endTime = LocalDateTime.now();
        YearMonth currentMonth = YearMonth.from(cursor);

        log.info("{} 데이터 생성 중...", currentMonth);

        while (cursor.isBefore(endTime)) {
            if (!YearMonth.from(cursor).equals(currentMonth)) {
                currentMonth = YearMonth.from(cursor);
                log.info("{} 데이터 생성 중...", currentMonth);
            }

            float intervalHours = (float) intervalMinutes / 60.0f;

            for (Device device : devices) {
                float usage = 0.0f;
                if (isDeviceOn(device, cursor)) {
                    usage = generateElecData(DeviceType.fromByte(device.getDeviceType()), DeviceStatus.fromByte(device.getStatus()), true);
                }

                elecBuffer.add(new ElectricityReading(device, cursor, usage));
            }

            for (Floor floor : floors) {
                float usage = generateWaterData(floor.getFloorNum(), cursor);
                waterBuffer.add(new WaterReading(floor, cursor, usage));
            }

            float gasUsage = generateGasData(buildingDataCache.getTotalPeople(), cursor);
            GasReading gasReading = new GasReading();
            gasReading.setBuilding(building);
            gasReading.setReadingTime(cursor);
            gasReading.setValue(gasUsage);
            gasBuffer.add(gasReading);

            int BATCH_SIZE = 2000;

            if (elecBuffer.size() >= BATCH_SIZE) {
                electricityReadingRepository.saveAll(elecBuffer);
                elecBuffer.clear();
            }
            if (waterBuffer.size() >= BATCH_SIZE) {
                waterReadingRepository.saveAll(waterBuffer);
                waterBuffer.clear();
            }
            if (gasBuffer.size() >= BATCH_SIZE) {
                gasReadingRepository.saveAll(gasBuffer);
                gasBuffer.clear();
            }

            cursor = cursor.plusMinutes(intervalMinutes);
        }

        if (!elecBuffer.isEmpty()) electricityReadingRepository.saveAll(elecBuffer);
        if (!waterBuffer.isEmpty()) waterReadingRepository.saveAll(waterBuffer);
        if (!gasBuffer.isEmpty()) gasReadingRepository.saveAll(gasBuffer);

        log.info("========== 과거 데이터 생성을 완료했습니다. ==========");
    }
}
