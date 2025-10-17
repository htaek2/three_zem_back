package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.data.dto.BillAnalysisDto;
import com.ThreeZem.three_zem_back.data.dto.BillStatDto;
import com.ThreeZem.three_zem_back.data.entity.ElectricityMonthlyUsage;
import com.ThreeZem.three_zem_back.data.entity.GasMonthlyUsage;
import com.ThreeZem.three_zem_back.data.entity.OtherBuilding;
import com.ThreeZem.three_zem_back.data.entity.WaterMonthlyUsage;
import com.ThreeZem.three_zem_back.repository.ElectricityMonthlyUsageRepository;
import com.ThreeZem.three_zem_back.repository.GasMonthlyUsageRepository;
import com.ThreeZem.three_zem_back.repository.OtherBuildingRepository;
import com.ThreeZem.three_zem_back.repository.WaterMonthlyUsageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final OtherBuildingRepository otherBuildingRepository;
    private final ElectricityMonthlyUsageRepository electricityMonthlyUsageRepository;
    private final GasMonthlyUsageRepository gasMonthlyUsageRepository;
    private final WaterMonthlyUsageRepository waterMonthlyUsageRepository;

    public ResponseEntity<BillStatDto> getBillStat() {

        int numOfData = 12;
        // 전국 월 평균 (12개월)
        // 지역 월 평균 (12개월)
        try {

            // 전국 월 평균
            List<OtherBuilding> buildingList = otherBuildingRepository.findAll();

            // 전체 빌딩의 12개월치 요금 리스트의 리스트
            List<List<Long>> datas = new ArrayList<>();

            // 빌딩 당 12개월치의 요금 리스트
            for (OtherBuilding building : buildingList) {
                LocalDateTime endTime = LocalDateTime.now();
                LocalDateTime startTime = endTime.minusMonths(numOfData);

                List<ElectricityMonthlyUsage> elecMonthlyUsages = electricityMonthlyUsageRepository.findByBuildingIdAndTimestampBetween(building.getId(), startTime, endTime);
                List<GasMonthlyUsage> gasMonthlyUsages = gasMonthlyUsageRepository.findByBuildingIdAndTimestampBetween(building.getId(), startTime, endTime);
                List<WaterMonthlyUsage> waterMonthlyUsages = waterMonthlyUsageRepository.findByBuildingIdAndTimestampBetween(building.getId(), startTime, endTime);

                List<Long> data = new ArrayList<>();

                for (int i = 0; i < numOfData; i++) {
                    long sum = (long) (elecMonthlyUsages.get(i).getUsage() + gasMonthlyUsages.get(i).getUsage() + waterMonthlyUsages.get(i).getUsage());
                    data.add(sum);
                }
                datas.add(data);
            }





            BillStatDto billAnalysisDto = new BillStatDto();

            return ResponseEntity.status(HttpStatus.OK).body(billAnalysisDto);
        }
        catch (Exception e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    public ResponseEntity<BillAnalysisDto> getBillAnalysis() {

        // 년 총합 상위 5%
        // 년 총합 평균
        // 우리 빌딩의 연 요금
        // 우리 빌딩의 상위 퍼센트
        return ResponseEntity.status(HttpStatus.OK).body(new BillAnalysisDto());
    }
}
