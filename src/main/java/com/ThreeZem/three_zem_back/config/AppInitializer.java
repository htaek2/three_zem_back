package com.ThreeZem.three_zem_back.config;

import com.ThreeZem.three_zem_back.data.constant.PowerConsumptionConst;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.BuildingConfigDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.DeviceConfigDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.FloorConfigDto;
import com.ThreeZem.three_zem_back.data.entity.*;
import com.ThreeZem.three_zem_back.data.enums.DeviceStatus;
import com.ThreeZem.three_zem_back.data.enums.DeviceType;
import com.ThreeZem.three_zem_back.repository.*;
import com.ThreeZem.three_zem_back.service.ApplicationStateService;
import com.ThreeZem.three_zem_back.service.SimulationLogicService;
import com.ThreeZem.three_zem_back.util.CommonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppInitializer implements ApplicationRunner {

    //<editor-fold desc="Repositories and Services">
    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final DeviceRepository deviceRepository;
    private final MemberRepository memberRepository;
    private final ResourceLoader resourceLoader;
    private final PasswordEncoder passwordEncoder;
    private final ElectricityReadingRepository electricityReadingRepository;
    private final WaterReadingRepository waterReadingRepository;
    private final GasReadingRepository gasReadingRepository;
    private final SimulationLogicService simulationLogicService;
    private final ApplicationStateService applicationStateService;
    //</editor-fold>

    @Value("${simulation.history.start-years-ago:3}")
    private int startYearsAgo;

    @Value("${simulation.history.interval-minutes:10}")
    private int intervalMinutes;

    private static final int BATCH_SIZE = 2000;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (buildingRepository.count() == 0) {
            log.info("\n[Init] 초기 DB 데이터 없음.. 데이터 생성 시작");
            createBuildingData();
            log.info("[Init] 초기 DB 데이터 생성 완료");
        }

        if (electricityReadingRepository.count() == 0) {
            generateHistoricalData();
        } else {
            log.info("[Init] 과거 에너지 데이터가 이미 존재하여 생성을 건너뜁니다.");
        }

        // 모든 장치 상태를 ON으로 설정
        setAllDevicesStatus(DeviceStatus.DEVICE_ON);

        // 과거 데이터 생성 및 장치 상태 설정이 모두 끝났음을 알림
        applicationStateService.setHistoricalDataGenerated(true);

        createBuildingIdSheet();
        log.info("[Init] 빌딩 ID 시트 생성 완료");
        log.info("[Init] 서버 초기화 완료.");
    }

    private void generateHistoricalData() {
        log.info("========== 과거 {}년치 데이터 생성을 시작합니다. ({}분 단위) ==========", startYearsAgo, intervalMinutes);

        Building building = buildingRepository.findAll().get(0);
        List<Floor> floors = floorRepository.findByBuilding(building);
        List<Device> devices = deviceRepository.findByFloorBuilding(building);

        List<ElectricityReading> elecBuffer = new ArrayList<>();
        List<WaterReading> waterBuffer = new ArrayList<>();
        List<GasReading> gasBuffer = new ArrayList<>();

        LocalDateTime cursor = LocalDateTime.now().minusYears(startYearsAgo);
        LocalDateTime endTime = LocalDateTime.now();
        YearMonth currentMonth = YearMonth.from(cursor);

        log.info("{} 데이터 생성 중...", currentMonth);

        while (cursor.isBefore(endTime)) {
            if (!YearMonth.from(cursor).equals(currentMonth)) {
                currentMonth = YearMonth.from(cursor);
                log.info("{} 데이터 생성 중...", currentMonth);
            }

            float intervalHours = (float) intervalMinutes / 60.0f;

            for (Device device : devices) {
                if (simulationLogicService.isDeviceOn(device, cursor)) {
                    float usage = simulationLogicService.applyNoise(PowerConsumptionConst.getPowerConsumption(DeviceType.fromByte(device.getDeviceType()).name()) * intervalHours);
                    ElectricityReading reading = new ElectricityReading();
                    reading.setDevice(device);
                    reading.setReadingTime(cursor);
                    reading.setValue(usage);
                    elecBuffer.add(reading);
                }
            }

            Map<Integer, Integer> peoplePerFloor = Map.of(1, 7, 2, 30, 3, 30, 4, 15);
            for (Floor floor : floors) {
                int people = (int) (peoplePerFloor.getOrDefault(floor.getFloorNum(), 0) * simulationLogicService.getPeopleFactor(cursor));
                float usage = simulationLogicService.applyNoise(people * PowerConsumptionConst.WATER_PER_PERSON * intervalHours);
                WaterReading reading = new WaterReading();
                reading.setFloor(floor);
                reading.setReadingTime(cursor);
                reading.setValue(usage);
                waterBuffer.add(reading);
            }

            int totalPeople = (int) (82 * simulationLogicService.getPeopleFactor(cursor));
            float gasUsage = simulationLogicService.applyNoise(totalPeople * PowerConsumptionConst.GAS_PER_PERSON * intervalHours * simulationLogicService.getGasSeasonalFactor(cursor.getMonth()));
            GasReading gasReading = new GasReading();
            gasReading.setBuilding(building);
            gasReading.setReadingTime(cursor);
            gasReading.setValue(gasUsage);
            gasBuffer.add(gasReading);

            if (elecBuffer.size() >= BATCH_SIZE) {
                electricityReadingRepository.saveAll(elecBuffer);
                elecBuffer.clear();
            }
            if (waterBuffer.size() >= BATCH_SIZE) {
                waterReadingRepository.saveAll(waterBuffer);
                waterBuffer.clear();
            }
            if (gasBuffer.size() >= BATCH_SIZE) {
                gasReadingRepository.saveAll(gasBuffer);
                gasBuffer.clear();
            }

            cursor = cursor.plusMinutes(intervalMinutes);
        }

        if (!elecBuffer.isEmpty()) electricityReadingRepository.saveAll(elecBuffer);
        if (!waterBuffer.isEmpty()) waterReadingRepository.saveAll(waterBuffer);
        if (!gasBuffer.isEmpty()) gasReadingRepository.saveAll(gasBuffer);

        log.info("========== 과거 데이터 생성을 완료했습니다. ==========");
    }

    public void setAllDevicesStatus(DeviceStatus status) {
        List<Device> allDevices = deviceRepository.findAll();
        for (Device device : allDevices) {
            device.setStatus(status.getValue());
        }
        deviceRepository.saveAll(allDevices);
        log.info("모든 장비의 상태를 '{}'(으)로 변경했습니다.", status.name());
    }

    private void createBuildingData() {
        try {
            Member adminMember = createDefaultMember();
            ObjectMapper objectMapper = new ObjectMapper();
            Resource resource = resourceLoader.getResource("classpath:data/building-data.json");
            InputStream inputStream = resource.getInputStream();
            BuildingConfigDto buildingConfig = objectMapper.readValue(inputStream, BuildingConfigDto.class);

            Building building = new Building();
            building.setMember(adminMember);
            building.setBuildingName(buildingConfig.getBuildingName());
            building.setAddress(buildingConfig.getAddress());
            building.setTotalArea((float) buildingConfig.getTotalArea());
            buildingRepository.save(building);

            for (FloorConfigDto floorConfig : buildingConfig.getFloors()) {
                Floor floor = new Floor();
                floor.setBuilding(building);
                floor.setFloorNum(floorConfig.getFloorNum());
                floorRepository.save(floor);

                for (DeviceConfigDto deviceConfig : floorConfig.getDevices()) {
                    Device device = new Device();
                    device.setFloor(floor);
                    DeviceType deviceType = DeviceType.valueOf(deviceConfig.getDeviceType());
                    device.setDeviceType(deviceType.getValue());
                    device.setDeviceName(deviceConfig.getDeviceName());
                    device.setInstalledTime(LocalDateTime.parse(deviceConfig.getInstalledTime(), CommonUtil.TimeUtil.getDateTimeFormatter()));
                    device.setStatus(DeviceStatus.DEVICE_OFF.getValue());
                    deviceRepository.save(device);
                }
            }
        } catch (Exception e) {
            log.error("[Init] 초기 빌딩 데이터 생성 실패", e);
        }
    }

    private Member createDefaultMember() {
        String adminId = "ad";
        String adminPw = "123";
        return memberRepository.findByEmail(adminId)
                .orElseGet(() -> {
                    Member member = new Member();
                    member.setUserName("기본 관리자");
                    member.setEmail(adminId);
                    member.setPassword(passwordEncoder.encode(adminPw));
                    return memberRepository.save(member);
                });
    }

    private void createBuildingIdSheet() {
        File file = new File("buildingCode.csv");
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            bw.write("buildingName,address,buildingId\n");
            for (var building : buildingRepository.findAll()) {
                bw.write(building.getBuildingName() + ",");
                bw.write(building.getAddress() + ",");
                bw.write(building.getId() + "\n");
            }
        } catch (Exception e) {
            log.error("[Init] 빌딩 ID 시트 생성 실패", e);
        }
    }
}