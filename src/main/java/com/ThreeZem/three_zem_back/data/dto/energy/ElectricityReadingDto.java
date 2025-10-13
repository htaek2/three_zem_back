package com.ThreeZem.three_zem_back.data.dto.energy;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ElectricityReadingDto {
    private Long deviceId;
    private LocalDateTime timestamp;
    private float usage;
}
