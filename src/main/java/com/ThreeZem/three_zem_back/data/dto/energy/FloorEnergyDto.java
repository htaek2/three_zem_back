package com.ThreeZem.three_zem_back.data.dto.energy;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FloorEnergyDto {
    /// 층 수
    private int floorNum;

    /// 수도 사용량
    private EnergyReadingDto waterUsage;

    /// 전자기기별 전력 사용량
    private List<DeviceEnergyDto> devices;
}
