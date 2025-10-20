package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.data.constant.ConfigConst;
import com.ThreeZem.three_zem_back.data.constant.EnergyPriceConst;
import com.ThreeZem.three_zem_back.data.dto.BillAnalysisDto;
import com.ThreeZem.three_zem_back.data.dto.BillStatDto;
import com.ThreeZem.three_zem_back.data.dto.energy.EnergyReadingDto;
import com.ThreeZem.three_zem_back.data.entity.ElectricityMonthlyUsage;
import com.ThreeZem.three_zem_back.data.entity.GasMonthlyUsage;
import com.ThreeZem.three_zem_back.data.entity.OtherBuilding;
import com.ThreeZem.three_zem_back.data.entity.WaterMonthlyUsage;
import com.ThreeZem.three_zem_back.data.enums.DateTimeType;
import com.ThreeZem.three_zem_back.repository.ElectricityMonthlyUsageRepository;
import com.ThreeZem.three_zem_back.repository.GasMonthlyUsageRepository;
import com.ThreeZem.three_zem_back.repository.OtherBuildingRepository;
import com.ThreeZem.three_zem_back.repository.WaterMonthlyUsageRepository;
import com.ThreeZem.three_zem_back.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ObjectInputFilter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final OtherBuildingRepository otherBuildingRepository;
    private final ElectricityMonthlyUsageRepository electricityMonthlyUsageRepository;
    private final GasMonthlyUsageRepository gasMonthlyUsageRepository;
    private final WaterMonthlyUsageRepository waterMonthlyUsageRepository;

    private final EnergyDataService energyDataService;

    private final int numOfData = 12;
    private final int ourLocationCode = 133; // 대전

    public ResponseEntity<BillStatDto> getBillStat() {

        try {
            // 전국 월 평균
            List<OtherBuilding> allbuildingList = otherBuildingRepository.findAll();

            // 전체 빌딩의 12개월치 요금 리스트의 리스트
            List<List<Long>> buildingsMonthlyPrices = new ArrayList<>();
            // 지역 빌디의 12개월치 요금
            List<List<Long>> localMonthlyPrices = new ArrayList<>();

            LocalDateTime endTime = LocalDateTime.now();
            LocalDateTime startTime = endTime.minusMonths(numOfData + 1);

            // 빌딩 당 12개월치의 요금 리스트
            for (OtherBuilding building : allbuildingList) {

                List<ElectricityMonthlyUsage> elecMonthlyUsages = electricityMonthlyUsageRepository.findByBuildingIdAndTimestampBetween(building.getId(), startTime, endTime);
                List<GasMonthlyUsage> gasMonthlyUsages = gasMonthlyUsageRepository.findByBuildingIdAndTimestampBetween(building.getId(), startTime, endTime);
                List<WaterMonthlyUsage> waterMonthlyUsages = waterMonthlyUsageRepository.findByBuildingIdAndTimestampBetween(building.getId(), startTime, endTime);

                if (elecMonthlyUsages.size() != gasMonthlyUsages.size() || elecMonthlyUsages.size() != waterMonthlyUsages.size()) {
                    log.warn("Building {} has inconsistent monthly usage data.", building.getId());
                    continue;
                }

                List<Long> monthlyPrices = new ArrayList<>();

                // 정확히는 각 에너지 데이터마다 각 매월 데이터를 불러오는 식으로 해야하는데 현재는 임의로 생성한 데이터라 모든 월 데이터가 있고, 순서도 맞으니 그냥 더함.
                for (int i = 0; i < elecMonthlyUsages.size(); i++) {
                    long monthlyPrice = (long) (elecMonthlyUsages.get(i).getUsage() * EnergyPriceConst.UNIT_PRICE_ELECTRICITY
                            + gasMonthlyUsages.get(i).getUsage() * EnergyPriceConst.UNIT_PRICE_GAS
                            + waterMonthlyUsages.get(i).getUsage() * EnergyPriceConst.UNIT_PRICE_WATER);
                    monthlyPrices.add(monthlyPrice);
                }
                buildingsMonthlyPrices.add(monthlyPrices);

                if (building.getLocationCode() == ourLocationCode) {
                    localMonthlyPrices.add(monthlyPrices);
                }
            }

            List<Long> allMonthlyAvg = new ArrayList<>();
            List<Long> localMonthlyAvg = new ArrayList<>();

            int actualNumOfData = buildingsMonthlyPrices.isEmpty() ? 0 : buildingsMonthlyPrices.get(0).size();

            // 전국 월 평균 금액
            for (int i = 0; i < actualNumOfData; i++) {
                Long sum = 0L;
                for (List<Long> monthly : buildingsMonthlyPrices) {
                    sum += monthly.get(i);
                }
                if (!buildingsMonthlyPrices.isEmpty()) {
                    allMonthlyAvg.add(Math.floorDiv(sum, buildingsMonthlyPrices.size()));
                }
            }

            // 지역 월 평균 금액
            for (int i = 0; i < actualNumOfData; i++) {
                Long sum = 0L;
                for (List<Long> monthly : localMonthlyPrices) {
                    sum += monthly.get(i);
                }
                if (!localMonthlyPrices.isEmpty()) {
                    localMonthlyAvg.add(Math.floorDiv(sum, localMonthlyPrices.size()));
                }
            }

            BillStatDto billAnalysisDto = new BillStatDto(buildingsMonthlyPrices.size(), localMonthlyPrices.size(), allMonthlyAvg, localMonthlyAvg);

            return ResponseEntity.status(HttpStatus.OK).body(billAnalysisDto);
        }
        catch (Exception e) {
            log.error("[ERROR] 분석 데이터 조회 중 에러 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    public ResponseEntity<BillAnalysisDto> getBillAnalysis() {

        try {
            List<OtherBuilding> allbuildingList = otherBuildingRepository.findAll();

            List<Long> sumList = new ArrayList<>();
            LocalDateTime endTime = LocalDateTime.now();
            LocalDateTime startTime = endTime.minusMonths(numOfData);

            // 모든 빌딩이 각자 1년치 평균 내기
            for (var building : allbuildingList) {

                List<ElectricityMonthlyUsage> elecMonthlyUsages = electricityMonthlyUsageRepository.findByBuildingIdAndTimestampBetween(building.getId(), startTime, endTime);
                List<GasMonthlyUsage> gasMonthlyUsages = gasMonthlyUsageRepository.findByBuildingIdAndTimestampBetween(building.getId(), startTime, endTime);
                List<WaterMonthlyUsage> waterMonthlyUsages = waterMonthlyUsageRepository.findByBuildingIdAndTimestampBetween(building.getId(), startTime, endTime);

                long sum = (long)(elecMonthlyUsages.stream().mapToDouble(ElectricityMonthlyUsage::getUsage).sum() * EnergyPriceConst.UNIT_PRICE_ELECTRICITY);
                sum += (long)(gasMonthlyUsages.stream().mapToDouble(GasMonthlyUsage::getUsage).sum() * EnergyPriceConst.UNIT_PRICE_GAS);
                sum += (long)(waterMonthlyUsages.stream().mapToDouble(WaterMonthlyUsage::getUsage).sum() * EnergyPriceConst.UNIT_PRICE_WATER);

                sumList.add(sum);
            }

            BillAnalysisDto billAnalysisDto = new BillAnalysisDto();

            // 평균 금액을 오름차순 정렬
            List<Long> sortedAvg = sumList.stream().sorted().toList();

            int numOfTotal = sortedAvg.size();
            billAnalysisDto.setTotal(numOfTotal);

            // 상위 5% 개수
            int numOfTopFivePer = (int) Math.floor(numOfTotal * 0.05);
            long topSum = 0L;
            long totalSum = 0L;

            for (int i = 0; i < sortedAvg.size(); i++) {
                if (i < numOfTopFivePer) {
                    topSum += sortedAvg.get(i);
                }
                totalSum += sortedAvg.get(i);
            }

            if (numOfTopFivePer > 0) {
                billAnalysisDto.setRowYearyPriceTop(topSum / numOfTopFivePer); // 상위 5%의 평균 금액
            } else {
                billAnalysisDto.setRowYearyPriceTop(-1L);
            }
            billAnalysisDto.setAvgYearyPrice(totalSum / sortedAvg.size()); // 전체 평균 금액
            // 우리 빌딩 1년 금액
            DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            List<EnergyReadingDto> ourPrice = energyDataService.getBillRangeData(startTime.format(f), endTime.format(f), DateTimeType.YEAR.getValue());

            long ourYearlyPrice = 0;
            for (EnergyReadingDto energyReadingDto : ourPrice) {
                if (energyReadingDto != null && energyReadingDto.getDatas() != null && !energyReadingDto.getDatas().isEmpty()) {
                    ourYearlyPrice += (long) energyReadingDto.getDatas().get(0).getUsage();
                }
            }
            billAnalysisDto.setOurYearyPrice(ourYearlyPrice);

            int rank = 0;
            for (int i = 0; i < sortedAvg.size(); i++) {
                if (sortedAvg.get(i) >= ourYearlyPrice) {
                    rank = i;
                    break;
                }
            }
            if(rank == 0 && !sortedAvg.isEmpty() && sortedAvg.get(0) < ourYearlyPrice) {
                rank = sortedAvg.size();
            }

            float percentage = (float) rank / sortedAvg.size() * 100;
            billAnalysisDto.setOurPercentage(percentage);

            return ResponseEntity.status(HttpStatus.OK).body(billAnalysisDto);
        }
        catch (Exception e) {
            log.error("[ERROR] 분석 데이터 조회 중 에러 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
