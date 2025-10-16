package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.data.dto.buildingConfig.BuildingConfigDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.DeviceConfigDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.FloorConfigDto;
import com.ThreeZem.three_zem_back.data.entity.*;
import com.ThreeZem.three_zem_back.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppInitializeService {

    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final DeviceRepository deviceRepository;
    private final MemberRepository memberRepository;

    private final ResourceLoader resourceLoader;
    private final PasswordEncoder passwordEncoder;

    /// 빌딩 설정 json 파일로부터 빌딩 데이터를 만들어 DB에 저장
    public void createBuildingData(Member adminMember) {
        try {
            // json 파싱
            String buildingConfigPath = "classpath:data/building-data.json";

            ObjectMapper objectMapper = new ObjectMapper();
            Resource resource = resourceLoader.getResource(buildingConfigPath);
            InputStream inputStream = resource.getInputStream();
            BuildingConfigDto buildingConfig = objectMapper.readValue(inputStream, BuildingConfigDto.class);

            // 빌딩 Entity 생성
            Building building = new Building(adminMember, buildingConfig);
            buildingRepository.save(building);

            // 층 Entity 생성
            for (FloorConfigDto floorConfig : buildingConfig.getFloors()) {
                Floor floor = new Floor(building, floorConfig);
                floorRepository.save(floor);

                // 장비 Entity 생성
                for (DeviceConfigDto deviceConfig : floorConfig.getDevices()) {
                    Device device = new Device(floor, deviceConfig);
                    deviceRepository.save(device);
                }
            }
        } catch (Exception e) {
            log.error("[Init] 초기 빌딩 데이터 생성 실패: {}", String.valueOf(e));
        }
    }

    /// 초기 관리자 계정 생성
    public Member createDefaultMember() {
        try {
            String adminId = "ad";
            String adminPw = "123";
            return memberRepository.findByEmail(adminId).orElseGet(() -> {
                Member member = new Member();
                member.setUserName("기본 관리자");
                member.setEmail(adminId);
                member.setPassword(passwordEncoder.encode(adminPw));
                return memberRepository.save(member);
            });
        }
        catch (Exception e) {
            log.error("[Init] 초기 관리자 계정 생성 실패: {}", String.valueOf(e));
            return null;
        }
    }

    /// 빌딩 목록 파일을 생성
    public void createBuildingIdSheet() {
        File file = new File("buildingCode.csv");
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            bw.write("buildingName,address,buildingId\n");
            for (var building : buildingRepository.findAll()) {
                bw.write(building.getBuildingName() + ",");
                bw.write(building.getAddress() + ",");
                bw.write(building.getId() + "\n");
            }
        } catch (Exception e) {
            log.error("[Init] 빌딩 ID 목록 파일 생성 실패", e);
        }
    }
}
