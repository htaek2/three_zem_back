package com.ThreeZem.three_zem_back.data.dto.energy;

import com.ThreeZem.three_zem_back.data.enums.ReadingTarget;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnergyRangeRequestDto {

    private String start;
    private String end;
    private ReadingTarget readingTarget;

}
