package com.ThreeZem.three_zem_back.config;

import com.ThreeZem.three_zem_back.data.entity.Member;
import com.ThreeZem.three_zem_back.repository.*;
import com.ThreeZem.three_zem_back.service.AppInitializeService;
import com.ThreeZem.three_zem_back.service.ApplicationStateService;
import com.ThreeZem.three_zem_back.service.DataGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppInitializer implements ApplicationRunner {

    private final AppInitializeService appInitializeService;
    private final ApplicationStateService applicationStateService;
    private final BuildingDataCache buildingDataCache;
    private final DataGenerationService dataGenerationService;

    private final BuildingRepository buildingRepository;
    private final DeviceRepository deviceRepository;
    private final ElectricityReadingRepository electricityReadingRepository;
    private final GasReadingRepository gasReadingRepository;

    private final boolean isDataCreate = true;

    /// 몇 년치 데이터를 만들지. 기본 2 = 2년 전부터 오늘까지
    private final int startYearsAgo = 3;

    /// 데이터 생성시간 단위. 기본 360 = 6시간
    private final int intervalMinutes = 60;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        log.info("\n");

        if (buildingRepository.count() == 0) {
            log.info("[INIT] 초기 DB 데이터 없음.. 데이터 생성 시작");

            // 관리자 계정 생성
            Member adminMember = appInitializeService.createDefaultMember();
            log.info("[INIT] 기본 관리자 계정 생성 및 저장 완료");

            // 빌딩 데이터 생성
            appInitializeService.createBuildingData(adminMember);
            log.info("[INIT] 빌딩 데이터 생성 및 저장 완료");

            log.info("[INIT] 초기 DB 데이터 생성 완료");
        }
        else {
            log.info("[INIT] 초기 데이터 OK");
        }

        buildingDataCache.init();

        if (isDataCreate) {
            // 과거 데이터 생성
            dataGenerationService.checkAndGenerateHistoricalData(startYearsAgo, intervalMinutes);
            dataGenerationService.checkAndGenerateOtherBuildingData();
        }
        else {
            log.info("[INIT] 과거 데이터 생성 Pass");
        }

        // 과거 데이터 생성 및 장치 상태 설정이 모두 끝났음을 알림
        applicationStateService.setDataGenerated(true);

        appInitializeService.createBuildingIdSheet();
        log.info("[INIT] 빌딩 ID 시트 생성 완료");

        log.info("[INIT] 서버 초기화 완료.");
    }

}