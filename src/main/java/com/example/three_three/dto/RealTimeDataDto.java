package com.example.three_three.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class RealTimeDataDto {
    private LocalDateTime timestamp;
    private double gasUsage;
    private Map<Integer, FloorData> floorData;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class FloorData {
        private double electricityUsage;
        private double waterUsage;
    }
}
