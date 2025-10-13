package com.ThreeZem.three_zem_back.data.dto.energy;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WaterReadingDto {
    private Long floorId;
    private LocalDateTime timestamp;
    private float usage;
}
