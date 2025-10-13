package com.ThreeZem.three_zem_back.controller;

import com.ThreeZem.three_zem_back.data.common.CustomUser;
import com.ThreeZem.three_zem_back.data.constant.ConfigConst;
import com.ThreeZem.three_zem_back.data.dto.energy.BuildingEnergyDto;
import com.ThreeZem.three_zem_back.service.DataGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalTime;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RealTimeDataController {
    // SSE(Server-Sent Event) 방식으로 구독한 클라이언트에게 설정한 시간마다 데이터를 전송한다.
    // MediaType.TEXT_EVENT_STREAM_VALUE 로 설정해야 sse 방식이 됨.

    private final DataGenerationService dataGenerationService;

    /// 실시간 에너지 사용량 조회
    @GetMapping(value = "/api/sse/energy/all", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ResponseEntity<BuildingEnergyDto>> getSseEnergyAll(Authentication auth) {

        try {
            UUID buildingId = UUID.fromString(((CustomUser)auth.getPrincipal()).getBuildingId());
            BuildingEnergyDto buildingEnergy = dataGenerationService.getBuildingEnergyData(buildingId);

            return Flux.interval(Duration.ofSeconds(ConfigConst.DATA_UPDATE_MS)).map(seq -> {
                return ResponseEntity.status(HttpStatus.OK).body(buildingEnergy);
            });
        }
        catch (Exception e) {
            log.error("[Error] buildingId 누락 또는 실시간 데이터 생성 오류");
            return Flux.interval(Duration.ofSeconds(ConfigConst.DATA_UPDATE_MS)).map(seq -> {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            });
        }

    }

    /// 실시간 금일 장비(전력) 누적 사용량 조회
    @GetMapping(value = "/api/sse/energy/day/device", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ResponseEntity<BuildingEnergyDto>> getSseEnergyDayDevice() {
        return Flux.interval(Duration.ofSeconds(ConfigConst.DATA_UPDATE_MS)).map(seq -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
    }

    /// 실시간 금일 가스 누적 사용량 조회
    @GetMapping(value = "/api/sse/energy/day/gas", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ResponseEntity<BuildingEnergyDto>> getSseEnergyDayGas() {
        return Flux.interval(Duration.ofSeconds(ConfigConst.DATA_UPDATE_MS)).map(seq -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
    }

    /// 실시간 금일 수도 누적 사용량 조회
    @GetMapping(value = "/api/sse/energy/day/water", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ResponseEntity<BuildingEnergyDto>> getSseEnergyDayWater() {
        return Flux.interval(Duration.ofSeconds(ConfigConst.DATA_UPDATE_MS)).map(seq -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
    }

    /// 실시간 금월 장비(전력) 누적 사용량 조회
    @GetMapping(value = "/api/sse/energy/month/device", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ResponseEntity<BuildingEnergyDto>> getSseEnergyMonthDevice() {
        return Flux.interval(Duration.ofSeconds(ConfigConst.DATA_UPDATE_MS)).map(seq -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
    }

    /// 실시간 금월 가스 누적 사용량 조회
    @GetMapping(value = "/api/sse/energy/month/gas", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ResponseEntity<BuildingEnergyDto>> getSseEnergyMonthGas() {
        return Flux.interval(Duration.ofSeconds(ConfigConst.DATA_UPDATE_MS)).map(seq -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
    }

    /// 실시간 금월 수도 누적 사용량 조회
    @GetMapping(value = "/api/sse/energy/month/water", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ResponseEntity<BuildingEnergyDto>> getSseEnergyMonthWater() {
        return Flux.interval(Duration.ofSeconds(ConfigConst.DATA_UPDATE_MS)).map(seq -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
    }

}
