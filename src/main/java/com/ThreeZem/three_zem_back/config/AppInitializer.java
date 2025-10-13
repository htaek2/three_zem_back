package com.ThreeZem.three_zem_back.config;

import com.ThreeZem.three_zem_back.data.entity.Member;
import com.ThreeZem.three_zem_back.repository.BuildingRepository;
import com.ThreeZem.three_zem_back.repository.DeviceRepository;
import com.ThreeZem.three_zem_back.repository.ElectricityReadingRepository;
import com.ThreeZem.three_zem_back.service.AppInitializeService;
import com.ThreeZem.three_zem_back.service.ApplicationStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppInitializer implements ApplicationRunner {

    private final AppInitializeService appInitializeService;
    private final ApplicationStateService applicationStateService;

    private final BuildingRepository buildingRepository;
    private final ElectricityReadingRepository electricityReadingRepository;
    private final DeviceRepository deviceRepository;

    private final boolean isDataCreate = false;

    /// 몇 년치 데이터를 만들지. 기본 2 = 2년 전부터 오늘까지
    private final int startYearsAgo = 2;

    /// 데이터 생성시간 단위. 기본 360 = 6시간
    private final int intervalMinutes = 360;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        log.info("\n");

        if (buildingRepository.count() == 0) {
            log.info("[Init] 초기 DB 데이터 없음.. 데이터 생성 시작");

            // 관리자 계정 생성
            Member adminMember = appInitializeService.createDefaultMember();
            log.info("[Init] 기본 관리자 계정 생성 및 저장 완료");

            // 빌딩 데이터 생성
            appInitializeService.createBuildingData(adminMember);
            log.info("[Init] 빌딩 데이터 생성 및 저장 완료");

            log.info("[Init] 초기 DB 데이터 생성 완료");
        }
        else {
            log.info("[Init] 초기 데이터 OK");
        }

        if (isDataCreate) {
            long dataNum = electricityReadingRepository.count();

            // 디바이스 개수에 따른 필요 전력 데이터 개수
            long deviceNum = deviceRepository.count();
            long need = startYearsAgo * 365 * 24 * 60 / intervalMinutes * deviceNum;

            // 전부 생성
            if (dataNum == 0) {
                log.info("[Init] 과거 데이터를 생성합니다.");
                // TODO
            }
            // 부분 생성
            else if (dataNum < need) {
                log.info("[Init] 누락된 과거 데이터를 생성합니다.");
                // TODO
            }
            else {
                log.info("[Init] 과거 데이터 OK");
            }
        }
        else {
            log.info("[Info] 과거 데이터 생성 Pass");
        }

        // 과거 데이터 생성 및 장치 상태 설정이 모두 끝났음을 알림
        applicationStateService.setDataGenerated(true);

        appInitializeService.createBuildingIdSheet();
        log.info("[Init] 빌딩 ID 시트 생성 완료");

        log.info("[Init] 서버 초기화 완료.");

    }

}