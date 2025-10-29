package com.ThreeZem.three_zem_back.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarbonPredictionDto {
    private int quarter;
    private float value;
}
