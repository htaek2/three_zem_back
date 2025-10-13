package com.ThreeZem.three_zem_back.data.dto.energy;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class GasReadingDto {
    private UUID buildingId;
    private LocalDateTime timestamp;
    private float usage;
}
