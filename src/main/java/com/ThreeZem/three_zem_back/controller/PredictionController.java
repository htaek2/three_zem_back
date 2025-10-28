package com.ThreeZem.three_zem_back.controller;

import com.ThreeZem.three_zem_back.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PredictionController {
    private final PredictionService predictionService;

    /// 금월, 금년 사용금액 예측 조회
    @GetMapping("/api/predict/bill")
    public String predictBill() {
        return predictionService.predictBill();
    }

    /// 금년 예상 탄소배출량 조회
    @GetMapping("/api/predict/carbon")
    public String predictCarbon() {
        return predictionService.predictCarbon();
    }

}