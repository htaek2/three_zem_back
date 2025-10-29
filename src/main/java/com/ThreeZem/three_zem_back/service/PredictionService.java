package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.data.constant.CarbonEmissionConst;
import com.ThreeZem.three_zem_back.data.constant.EnergyPriceConst;
import com.ThreeZem.three_zem_back.data.dto.CarbonPredictionDto;
import com.ThreeZem.three_zem_back.data.dto.MonthDto;
import com.ThreeZem.three_zem_back.data.dto.PredictBillDto;
import com.ThreeZem.three_zem_back.data.dto.YearDto;
import com.ThreeZem.three_zem_back.data.dto.energy.PredictionResponseDto;
import com.ThreeZem.three_zem_back.data.dto.energy.ReadingDto;
import com.ThreeZem.three_zem_back.data.enums.DateTimeType;
import com.ThreeZem.three_zem_back.util.TimeUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PredictionService {

    private final EnergyDataService energyDataService;
    private final WebClient webClient;

    private PredictionResponseDto predictionCache;

    @PostConstruct
    public void initializePredictions() {
        try {
            this.predictionCache = fetchPredictions();
            log.info("[INIT] 예측 데이터 캐싱 OK");
        } catch (Exception e) {
            log.error("[ERROR] 예측 데이터 캐시 업데이트 실패: {}", e.getMessage());
        }
    }

    private PredictionResponseDto fetchPredictions() {
        LocalDateTime now = LocalDateTime.now();
        String startTime = now.minusYears(3).format(TimeUtil.getDateTimeFormatter());
        String nowStr = now.format(TimeUtil.getDateTimeFormatter());

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("electricityReadings", energyDataService.getElecRangeData(startTime, nowStr, DateTimeType.HOUR.getValue()));
        requestData.put("gasReadings", energyDataService.getGasRangeData(startTime, nowStr, DateTimeType.HOUR.getValue()));
        requestData.put("waterReadings", energyDataService.getWaterRangeData(startTime, nowStr, DateTimeType.HOUR.getValue()));

        PredictionResponseDto responseDto = webClient.post()
                .uri("/api/v1/predict/")
                .bodyValue(requestData)
                .retrieve()
                .bodyToMono(PredictionResponseDto.class)
                .block();

        if (responseDto != null) {
            int elecCnt = responseDto.getElecPredictions() != null ? responseDto.getElecPredictions().size() : 0;
            int gasCnt = responseDto.getGasPredictions() != null ? responseDto.getGasPredictions().size() : 0;
            int waterCnt = responseDto.getWaterPredictions() != null ? responseDto.getWaterPredictions().size() : 0;
            log.info("[INFO] 머신러닝 서버로부터의 응답 수신: Elec: {}, Gas: {}, Water: {}", elecCnt, gasCnt, waterCnt);
        } else {
            log.error("[ERROR] 머신러닝 서버로부터 값을 받지 못함");
        }

        return responseDto;
    }

    public PredictBillDto predictBill() {
        Map<LocalDate, Map<String, Double>> dailyUsage = getDailyUsageMap();
        if (dailyUsage.isEmpty()) {
            log.warn("계산을 위한 집계된 사용량 데이터가 없습니다.");
            return null;
        }

        List<MonthDto> monthPredictions = calculateMonthPredictions(dailyUsage);
        List<YearDto> yearPredictions = calculateYearPredictions(dailyUsage);

        return PredictBillDto.builder()
                .month(monthPredictions)
                .year(yearPredictions)
                .build();
    }

    public List<CarbonPredictionDto> predictCarbon() {
        Map<LocalDate, Map<String, Double>> dailyUsage = getDailyUsageMap();
        if (dailyUsage.isEmpty()) {
            log.warn("계산을 위한 집계된 사용량 데이터가 없습니다.");
            return new ArrayList<>();
        }

        float[] quarterlyEmissions = new float[4];
        dailyUsage.forEach((date, usage) -> {
            float dailyEmission = calculateCarbon(usage);
            int quarter = (date.getMonthValue() - 1) / 3;
            quarterlyEmissions[quarter] += dailyEmission;
        });

        List<CarbonPredictionDto> carbonPredictions = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            carbonPredictions.add(CarbonPredictionDto.builder()
                    .quarter(i + 1)
                    .value(quarterlyEmissions[i])
                    .build());
        }
        return carbonPredictions;
    }

    private Map<LocalDate, Map<String, Double>> getDailyUsageMap() {
        if (predictionCache == null) {
            log.warn("예측 데이터 캐시가 비어있습니다.");
            initializePredictions();
        }

        Map<LocalDate, Map<String, Double>> dailyUsage = new HashMap<>();
        groupReadingsByDay(dailyUsage, predictionCache.getElecPredictions(), "elec");
        groupReadingsByDay(dailyUsage, predictionCache.getGasPredictions(), "gas");
        groupReadingsByDay(dailyUsage, predictionCache.getWaterPredictions(), "water");
        return dailyUsage;
    }

    private void groupReadingsByDay(Map<LocalDate, Map<String, Double>> dailyUsage, List<ReadingDto> readings, String type) {
        if (readings == null) return;
        for (ReadingDto reading : readings) {
            LocalDate date = reading.getTimestamp().toLocalDate();
            dailyUsage.computeIfAbsent(date, k -> new HashMap<>()).merge(type, (double) reading.getUsage(), Double::sum);
        }
    }

    private List<MonthDto> calculateMonthPredictions(Map<LocalDate, Map<String, Double>> dailyUsage) {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        return today.datesUntil(endOfMonth.plusDays(1))
                .map(date -> {
                    Map<String, Double> usage = dailyUsage.getOrDefault(date, Map.of());
                    double dailyCost = calculateCost(usage);
                    return MonthDto.builder()
                            .date(date.toString())
                            .value(dailyCost)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<YearDto> calculateYearPredictions(Map<LocalDate, Map<String, Double>> dailyUsage) {
        double[] quarterlyCosts = new double[4];
        dailyUsage.forEach((date, usage) -> {
            double dailyCost = calculateCost(usage);
            int quarter = (date.getMonthValue() - 1) / 3;
            quarterlyCosts[quarter] += dailyCost;
        });

        List<YearDto> yearPredictions = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            yearPredictions.add(YearDto.builder()
                    .quarter((i + 1) + "분기")
                    .value(quarterlyCosts[i])
                    .build());
        }
        return yearPredictions;
    }

    private double calculateCost(Map<String, Double> usage) {
        double elecCost = usage.getOrDefault("elec", 0.0) * EnergyPriceConst.UNIT_PRICE_ELECTRICITY;
        double gasCost = usage.getOrDefault("gas", 0.0) * EnergyPriceConst.UNIT_PRICE_GAS;
        double waterCost = usage.getOrDefault("water", 0.0) * EnergyPriceConst.UNIT_PRICE_WATER;
        return elecCost + gasCost + waterCost;
    }

    private float calculateCarbon(Map<String, Double> usage) {
        float elecCarbon = (float) (usage.getOrDefault("elec", 0.0) * CarbonEmissionConst.AMOUNT_CARBON_ELECTRICITY);
        float gasCarbon = (float) (usage.getOrDefault("gas", 0.0) * CarbonEmissionConst.AMOUNT_CARBON_GAS);
        float waterCarbon = (float) (usage.getOrDefault("water", 0.0) * CarbonEmissionConst.AMOUNT_CARBON_WATER);
        return elecCarbon + gasCarbon + waterCarbon;
    }
}
