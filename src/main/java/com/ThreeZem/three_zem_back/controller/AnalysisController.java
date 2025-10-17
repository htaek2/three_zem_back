package com.ThreeZem.three_zem_back.controller;

import com.ThreeZem.three_zem_back.data.dto.BillAnalysisDto;
import com.ThreeZem.three_zem_back.data.dto.BillStatDto;
import com.ThreeZem.three_zem_back.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    /// 12개월 월 평균 사용금액 조회
    @GetMapping("/api/bill/stat")
    public ResponseEntity<BillStatDto> getBillStat() {
        return analysisService.getBillStat();
    }

    /// 12개월 사용금액 분석 조회
    @GetMapping("/api/bill/analysis")
    public ResponseEntity<BillAnalysisDto> getBillAnalysis() {
        return analysisService.getBillAnalysis();
    }

}
