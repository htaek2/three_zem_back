package com.ThreeZem.three_zem_back.data.dto.energy;

import com.ThreeZem.three_zem_back.data.enums.DeviceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/// 실시간 에너지 데이터 전송 시 사용
public class DeviceEnergyDto {
    private Long deviceId;
    private DeviceType deviceType;
    private String deviceName;
    private EnergyReadingDto electricityUsage;
}
