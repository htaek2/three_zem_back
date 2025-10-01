package com.example.three_three.service;

import com.example.three_three.dto.RealTimeDataDto;
import com.example.three_three.util.building.BuildingInfo;
import com.example.three_three.util.building.FloorInfo;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

// 빌딩 전력, 가스, 수도의 임의 사용량을 생성하는 서비스
@Service
public class MockDataService {

    // 빌딩 정보
    private BuildingInfo buildingInfo;
    private final Random random = new Random();

    // 각 기기당 소비 전력
    private static final double COMPUTER_POWER = 0.2; // 200W
    private static final double MONITOR_POWER = 0.05; // 50W
    private static final double HVAC_POWER = 3.0;     // 3kW
    private static final double LIGHT_POWER_PER_SQM = 0.011; // 제곱미터 당 11W 소비

    // 수도와 가스는 사용 인원수에 비례해서 사용량 생성
    private static final double WATER_PER_PERSON = 0.01; // 인당 10L
    private static final double GAS_PER_PERSON = 0.05; // 인당 5m^3/h

    public MockDataService() {
        // 데이터베이스에서 빌딩 정보 가져오기

        





        // 빌딩 객체 생성
        buildingInfo.setName("토리 빌딩");
        buildingInfo.setTotalFloors(4);

        // 층 객체 생성
        Map<Integer, FloorInfo> floors = new HashMap<>();

        // 층 수, 방 개수, 최대 사용 인원, 컴퓨터 개수, 에어컨 개수, 조명 개수
        floors.put(1, new FloorInfo(1, 2, 7, 9, 2, 18));
        floors.put(2, new FloorInfo(2, 2, 30, 50, 2, 23));
        floors.put(3, new FloorInfo(3, 2, 30, 50, 2, 23));
        floors.put(4, new FloorInfo(4, 1, 15, 25, 1, 14));

        buildingInfo.setFloors(floors);
    }

    public RealTimeDataDto generateRealTimeData() {
        LocalDateTime now = LocalDateTime.now();
        Map<Integer, RealTimeDataDto.FloorData> floorDataMap = new HashMap<>();

        for (FloorInfo floorInfo : buildingInfo.getFloors().values()) {
            double electricity = calculateElectricity(floorInfo, now);
            double water = calcWater(floorInfo, now);
            floorDataMap.put(floorInfo.getFloorNum(), new RealTimeDataDto.FloorData(electricity, water));
        }

        double gas = calcGas(now);

        return new RealTimeDataDto(now, gas, floorDataMap);
    }

    // 사용기기 합산 전력 소비량 계산
    // 각 기기의 전원에 따라 전력 소비량을 계산한다.
    private double calcElectricity(FloorInfo floor, LocalDateTime now) {
        double usage = 1.0; // 기타 빌딩 유지에 필요한 전력들

        if (!isOperatingHours(now)) {
            return addNoise(usage, 0.01);
        }
    }

    // 외부 빌딩 전력 소비량 계산
    // 빌딩 정보에 따라 총합 전력 소비량을 계산한다. 각 기기별 전원 유무 상관없는 외부 빌딩 전력 사용량 계산시 사용.
    private double calcExternalElectricity(FloorInfo floor, LocalDateTime now) {
        double usage = 1.0; // 기타 빌딩 유지에 필요한 전력들

        if (!isOperatingHours(now)) {
            return addNoise(usage, 0.01);
        }

        double timeFactor = getTimeOfDayFactor(now);
        double seasonalFactor = getSeasonalFactor(now);

        // 조명 전력 사용량
        usage += (LIGHT_POWER_PER_SQM * 100) * timeFactor;
        // 컴퓨터 + 모니터 전력 사용량
        usage += (floor.getNumOfComputerSets() * (COMPUTER_POWER + MONITOR_POWER)) * 0.8 * timeFactor; // Assume 80% are on
        // 냉난방 기기 전력 사용량
        usage += (floor.getNumOfHvacs() * HVAC_POWER) * seasonalFactor * timeFactor;
        
        // 특수 상황 발생(사용량 급증 이벤트)
        if (random.nextDouble() < 0.05) {
            usage *= (1 + random.nextDouble() * 0.5);
        }

        return addNoise(usage / 360, 0.05);
    }

    // 수도 사용량 계산
    private double calcWater(FloorInfo floor, LocalDateTime now) {
        if (!isOperatingHours(now)) {
            return 0.0;
        }

        double usage = floor.getMaxOccupants() * WATER_PER_PERSON * getTimeOfDayFactor(now);
        
        // 점심 시간대 급증
        if (now.getHour() >= 12 && now.getHour() < 14) {
            usage *= 1.5;
        }

        return addNoise(usage / 360, 0.1);
    }

    // 가스 사용량 계산
    private double calcGas(LocalDateTime now) {
        if (!isOperatingHours(now)) {
            return 0.0;
        }

        // 전체 사용 인원
        int totalOccupants = buildingInfo.getFloors().values().stream().mapToInt(FloorInfo::getMaxOccupants).sum();
        double usage = totalOccupants * GAS_PER_PERSON * getSeasonalFactor(now);

        return addNoise(usage / 360, 0.05);
    }

    // 빌딩 Open 여부 반환
    private boolean isOperatingHours(LocalDateTime now) {
        DayOfWeek day = now.getDayOfWeek();
        int hour = now.getHour();
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY && hour >= 8 && hour < 21;
    }

    // 시간대별 사용량 변화 계수 반환
    private double getTimeOfDayFactor(LocalDateTime now) {
        int hour = now.getHour();

        // 오전 시간대
        if (hour >= 9 && hour < 12) {
            return 0.9;
        } 
        // 점심 시간대
        if (hour >= 12 && hour < 13) {
            return 0.6;
        }
        // 오후 시간대(피크)
        if (hour >= 13 && hour < 18) {
            return 1.0;
        }
        // 새벽, 밤 시간대
        else {
            return 0.3;
        }
    }

    // 계절별 사용량 변화 계수 반환
    private double getSeasonalFactor(LocalDateTime now) {
        Month month = now.getMonth();

        // 여름일 때
        if (month == Month.JUNE || month == Month.JULY || month == Month.AUGUST) {
            return 1.0;
        }
        // 겨울일 때
        else if (month == Month.DECEMBER || month == Month.JANUARY || month == Month.FEBRUARY) {
            return 0.8;
        }
        // 봄, 가을일 때
        else {
            return 0.4;
        }        
    }

    // 사용량에 노이즈 추가
    private double addNoise(double value, double percentage) {
        double noise = (random.nextDouble() * 2 - 1) * value * percentage;
        return Math.max(0, value + noise);
    }
}
