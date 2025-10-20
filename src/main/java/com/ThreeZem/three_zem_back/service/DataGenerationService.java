package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.config.BuildingDataCache;
import com.ThreeZem.three_zem_back.controller.BuildingController;
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
    private final OtherBuildingRepository otherBuildingRepository;
    private final ElectricityMonthlyUsageRepository electricityMonthlyUsageRepository;
    private final GasMonthlyUsageRepository gasMonthlyUsageRepository;
    private final WaterMonthlyUsageRepository waterMonthlyUsageRepository;

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

    public void checkAndGenerateHistoricalData(int startYearsAgo, int intervalMinutes) {
        LocalDateTime startTime = LocalDateTime.now().minusYears(startYearsAgo);

        // 분 단위로 총 시간 계산
        long totalMinutes = (long) startYearsAgo * 365 * 24 * 60;
        long expectedReadings = totalMinutes / intervalMinutes;

        long actualReadings = gasReadingRepository.countByReadingTimeAfter(startTime);

        log.info("[INIT] 과거 데이터 확인: 예상 데이터 수 ({}년, {}분 간격): {}, 실제 데이터 수: {}", startYearsAgo, intervalMinutes, expectedReadings, actualReadings);

        // 실제 데이터가 예상치의 90% 미만일 경우에만 데이터 생성
        if (actualReadings < expectedReadings * 0.9) {
            log.info("[INIT] 실제 데이터가 예상치보다 부족하여 과거 데이터 생성을 시작합니다.");
            generateHistoricalData(startYearsAgo, intervalMinutes);
        } else {
            log.info("[INIT] 과거 데이터가 충분히 존재하므로, 데이터 생성을 건너뜁니다.");
        }
    }

    /// 과거 데이터 생성
    public void generateHistoricalData(int startYearsAgo, int intervalMinutes) {

        log.info("[DATA] 과거 {}년치 데이터 생성을 시작합니다. ({}분 단위)", startYearsAgo, intervalMinutes);

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

            // Corrected Electricity Usage Calculation
            for (Device device : devices) {
                float usage = 0.0f;
                if (isDeviceOn(device, cursor)) {
                    float powerKw = PowerConsum.getPowerConsumption(DeviceType.fromByte(device.getDeviceType()));
                    usage = applyNoise(powerKw * intervalHours);
                }
                elecBuffer.add(new ElectricityReading(device, cursor, usage));
            }

            // Corrected Water Usage Calculation
            for (Floor floor : floors) {
                int peopleOnFloor = (int) (buildingDataCache.getPeoplePerFloor(floor.getFloorNum()) * getPeopleFactor(cursor));
                float waterUsage = peopleOnFloor * PowerConsum.WATER_PER_PERSON * intervalHours;
                waterBuffer.add(new WaterReading(floor, cursor, applyNoise(waterUsage)));
            }

            // Corrected Gas Usage Calculation
            int totalPeople = (int) (buildingDataCache.getTotalPeople() * getPeopleFactor(cursor));
            float gasUsage = totalPeople * PowerConsum.GAS_PER_PERSON * intervalHours * getGasSeasonalFactor(cursor.getMonth());
            GasReading gasReading = new GasReading();
            gasReading.setBuilding(building);
            gasReading.setReadingTime(cursor);
            gasReading.setValue(applyNoise(gasUsage));
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

        log.info("과거 데이터 생성을 완료했습니다.");
    }

    @Transactional
    public void checkAndGenerateOtherBuildingData() {
        if (otherBuildingRepository.count() == 0) {
            log.info("[INIT] 비교군 빌딩 데이터가 없어 생성을 시작합니다.");
            List<OtherBuilding> otherBuildings = generateOtherBuildings();
            generateOtherBuildingHistoricalData(otherBuildings);
        } else {
            log.info("[INIT] 비교군 빌딩 데이터가 이미 존재합니다.");
        }
    }

    private List<OtherBuilding> generateOtherBuildings() {
        log.info("[INIT] 비교군 빌딩 생성을 시작합니다.");
        List<OtherBuilding> otherBuildings = new ArrayList<>();
        BuildingConfigDto mainBuilding = buildingDataCache.getBuildingDto();

        // 지역별 코드와 비중 설정
        Map<Integer, Integer> locationDistribution = Map.of(
                108, 25, // 서울
                133, 10, // 대전
                159, 10, // 광주
                143, 10, // 부산
                112, 5,  // 대구
                119, 5,  // 인천
                131, 5,  // 울산
                184, 20, // 경기도
                156, 10  // 충청도
        );
        List<Integer> baseLocationPool = new ArrayList<>();
        locationDistribution.forEach((code, weight) -> {
            for (int i = 0; i < weight; i++) {
                baseLocationPool.add(code);
            }
        });

        // 1000개 데이터 생성
        List<Integer> locationPool = new ArrayList<>(1000);
        for (int i = 0; i < 10; i++) {
            locationPool.addAll(baseLocationPool);
        }
        Collections.shuffle(locationPool);

        for (int i = 1; i <= 1000; i++) {
            OtherBuilding building = new OtherBuilding();
            building.setBuildingName("비교 빌딩 " + i + "호");
            building.setLocationCode(locationPool.get(i-1));

            building.setUsagePeople(applyVariation((int)(buildingDataCache.getTotalPeople() * 0.8), 0.2));

            int totalLow = 0, totalMid = 0, totalHigh = 0;
            for (FloorConfigDto floor : mainBuilding.getFloors()) {
                for (DeviceConfigDto device : floor.getDevices()) {
                    if (device.getDeviceType() == DeviceType.LIGHT) totalLow++;
                    else if (device.getDeviceType() == DeviceType.COMPUTER) totalMid++;
                    else if (device.getDeviceType() == DeviceType.AIR_CONDITIONER) totalHigh++;
                }
            }

            building.setNumOfLowPowerDevices(applyVariation((int)(totalLow * 0.8), 0.2));
            building.setNumOfMidPowerDevices(applyVariation((int)(totalMid * 0.8), 0.2));
            building.setNumOfHighPowerDevices(applyVariation((int)(totalHigh * 0.8), 0.2));

            building.setNumOfWaterUseSpot(applyVariation((int)(mainBuilding.getFloors().size() * 2 * 0.8), 0.2));
            building.setNumOfGasUseSpot(applyVariation(1, 0.2));

            otherBuildings.add(building);
        }

        otherBuildingRepository.saveAll(otherBuildings);
        log.info("[INIT] 비교군 빌딩 생성 및 저장 완료.");
        return otherBuildings;
    }

    private void generateOtherBuildingHistoricalData(List<OtherBuilding> otherBuildings) {
        log.info("[INIT] 비교군 빌딩의 과거 3년치 월별 데이터 생성을 시작합니다.");
        List<ElectricityMonthlyUsage> elecUsages = new ArrayList<>();
        List<GasMonthlyUsage> gasUsages = new ArrayList<>();
        List<WaterMonthlyUsage> waterUsages = new ArrayList<>();

        float ranFactorMax = 0.62f;
        float ranFactorMin = 0.09f;

        LocalDateTime threeYearsAgo = LocalDateTime.now().minusYears(3);

        for (OtherBuilding building : otherBuildings) {
            YearMonth cursorMonth = YearMonth.from(threeYearsAgo);
            YearMonth endMonth = YearMonth.from(LocalDateTime.now());

            while (cursorMonth.isBefore(endMonth)) {
                LocalDateTime timestamp = cursorMonth.atDay(1).atStartOfDay();
                Month month = cursorMonth.getMonth();

                float baseElecUsage = (building.getNumOfLowPowerDevices() * 55) + (building.getNumOfMidPowerDevices() * 350) + (building.getNumOfHighPowerDevices() * 1200); // monthly kWh
                float baseWaterUsage = building.getUsagePeople() * 0.3f;
                float baseGasUsage = building.getUsagePeople() * 0.1f;

                float elecFactor = 1.0f + (getACSeasonalFactor(month) - 0.6f);
                float waterFactor = 1.0f;
                float gasFactor = getGasSeasonalFactor(month);

                float noisyElec = applyNoise(baseElecUsage * elecFactor) * (1.0f + (float) random.nextGaussian() * 0.1f);
                float noisyWater = applyNoise(baseWaterUsage * waterFactor) * (1.0f + (float) random.nextGaussian() * 0.1f);
                float noisyGas = applyNoise(baseGasUsage * gasFactor) * (1.0f + (float) random.nextGaussian() * 0.2f);

                float finalElec = noisyElec * (random.nextFloat() * (ranFactorMax - ranFactorMin) + ranFactorMin);
                float finalWater = noisyWater * (random.nextFloat() * (ranFactorMax - ranFactorMin) + ranFactorMin);
                float finalGas = noisyGas * (random.nextFloat() * (ranFactorMax - ranFactorMin) + ranFactorMin);

                elecUsages.add(new ElectricityMonthlyUsage(null, building.getId(), timestamp, finalElec));
                gasUsages.add(new GasMonthlyUsage(null, building.getId(), timestamp, finalGas));
                waterUsages.add(new WaterMonthlyUsage(null, building.getId(), timestamp, finalWater));

                if (elecUsages.size() >= 500) {
                    electricityMonthlyUsageRepository.saveAll(elecUsages);
                    gasMonthlyUsageRepository.saveAll(gasUsages);
                    waterMonthlyUsageRepository.saveAll(waterUsages);
                    elecUsages.clear();
                    gasUsages.clear();
                    waterUsages.clear();
                }
                cursorMonth = cursorMonth.plusMonths(1);
            }
        }

        if (!elecUsages.isEmpty()) {
            electricityMonthlyUsageRepository.saveAll(elecUsages);
            gasMonthlyUsageRepository.saveAll(gasUsages);
            waterMonthlyUsageRepository.saveAll(waterUsages);
        }
        log.info("[INIT] 비교군 빌딩 과거 데이터 생성 완료");
    }

    private int applyVariation(int baseValue, double percentage) {
        double variation = (random.nextDouble() * 2 - 1) * percentage;
        return (int) (baseValue * (1 + variation));
    }
}
