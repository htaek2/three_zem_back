package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.data.dto.energy.PredictionResponseDto;
import com.ThreeZem.three_zem_back.data.enums.DateTimeType;
import com.ThreeZem.three_zem_back.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PredictionService {

    private final EnergyDataService energyDataService;
    private final WebClient webClient;

    private PredictionResponseDto predictionCache;

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
            int elecCnt = responseDto.getElecPredictions().size();
            int gasCnt = responseDto.getGasPredictions().size();
            int waterCnt = responseDto.getWaterPredictions().size();
            log.info("[INFO] 머신러닝 서버로부터의 응답 수신: {}, {}, {}", elecCnt, gasCnt, waterCnt);
        }
        else {
            log.error("[ERROR] 머신러닝 서버로부터 값을 받지 못함");
        }

        return responseDto;
    }

    public String predictBill() {
        // TODO: 캐시된 List<ReadingDto> 데이터에서 요금 관련 값을 계산하여 반환해야 합니다.
        return predictionCache != null ? predictionCache.toString() : null;
    }

    public String predictCarbon() {
        // TODO: 캐시된 List<ReadingDto> 데이터에서 탄소 배출 관련 값을 계산하여 반환해야 합니다.
        return predictionCache != null ? predictionCache.toString() : null;
    }
}
