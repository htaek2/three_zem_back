package com.ThreeZem.three_zem_back.data.constant;

import com.ThreeZem.three_zem_back.data.enums.DeviceType;

/// 각 기기당 소비 전력
public class PowerConsum {
    public static final float COMPUTER_POWER = 0.37f; // 370W(모니터 포함)
    public static final float AC_POWER = 3.0f;     // 3000W
    public static final float LIGHT_POWER = 0.05f; // 50W

    // 수도와 가스는 사용 인원수에 비례해서 사용량 생성
    public static final float WATER_PER_PERSON = 0.01f; // 인당 10L
    public static final float GAS_PER_PERSON = 0.05f; // 인당 5m^3/h

    public static float getPowerConsumption(DeviceType deviceType) {
        switch (deviceType) {
            case COMPUTER:
                return COMPUTER_POWER;
            case AIR_CONDITIONER:
                return AC_POWER;
            case LIGHT:
                return LIGHT_POWER;
            default:
                return 0;
        }
    }
}
