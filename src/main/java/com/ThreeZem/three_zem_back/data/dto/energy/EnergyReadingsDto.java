package com.ThreeZem.three_zem_back.data.dto.energy;

import com.ThreeZem.three_zem_back.data.enums.EnergyType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/// 시간/일/월/년 에너지 데이터 조회 시 사용
public class EnergyReadingsDto {

    /// 에너지 유형
    private EnergyType energyType;

    /// 사용량 데이터 리스트
    private List<ReadingDto> datas;
}
