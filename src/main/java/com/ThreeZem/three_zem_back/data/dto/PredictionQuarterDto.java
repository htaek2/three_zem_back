package com.ThreeZem.three_zem_back.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PredictionQuarterDto {
    private Integer quarter;
    private float value;
}
