package com.ThreeZem.three_zem_back.data.dto.energy;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
/// 실시간 에너지 데이터 전송 시 사용
public class BuildingEnergyDto {
    /// 가스 사용량
    private EnergyReadingDto gasUsage;

    /// 층별 사용량
    private List<FloorEnergyDto> floors;
}