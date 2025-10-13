package com.ThreeZem.three_zem_back.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ApplicationStateService {

    private final AtomicBoolean historicalDataGenerated = new AtomicBoolean(false);

    public boolean isHistoricalDataGenerated() {
        return historicalDataGenerated.get();
    }

    public void setHistoricalDataGenerated(boolean isGenerated) {
        this.historicalDataGenerated.set(isGenerated);
        if (isGenerated) {
            System.out.println("[State] 과거 데이터 생성 완료. 실시간 데이터 생성을 시작합니다.");
        }
    }
}
