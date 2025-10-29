package com.ThreeZem.three_zem_back.controller;

import com.ThreeZem.three_zem_back.data.dto.CarbonPredictionDto;
import com.ThreeZem.three_zem_back.data.dto.PredictBillDto;
import com.ThreeZem.three_zem_back.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PredictionController {
    private final PredictionService predictionService;

    /// 금월, 금년 사용금액 예측 조회
    @GetMapping("/api/predict/bill")
    public ResponseEntity<PredictBillDto> predictBill() {
        PredictBillDto predictBillDto = predictionService.predictBill();

        if (predictBillDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        else {
            return ResponseEntity.status(HttpStatus.OK).body(predictBillDto);
        }
    }

    /// 금년 예상 탄소배출량 조회
    @GetMapping("/api/predict/carbon")
    public ResponseEntity<List<CarbonPredictionDto>> predictCarbon() {
        List<CarbonPredictionDto> res = predictionService.predictCarbon();

        if (res == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        else {
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
    }

}