package com.ThreeZem.three_zem_back.data.dto.energy;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PredictionResponseDto {
    private List<ReadingDto> elecPredictions;
    private List<ReadingDto> gasPredictions;
    private List<ReadingDto> waterPredictions;
}
