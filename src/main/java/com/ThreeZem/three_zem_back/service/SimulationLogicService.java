package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.data.entity.Device;
import com.ThreeZem.three_zem_back.data.enums.DeviceType;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Random;

@Service
public class SimulationLogicService {

    private final Random random = new Random();

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
