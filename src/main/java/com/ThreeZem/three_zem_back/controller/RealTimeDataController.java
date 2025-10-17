package com.ThreeZem.three_zem_back.controller;

import com.ThreeZem.three_zem_back.data.common.CustomUser;
import com.ThreeZem.three_zem_back.data.constant.ConfigConst;
import com.ThreeZem.three_zem_back.data.dto.WeatherDto;
import com.ThreeZem.three_zem_back.data.dto.energy.BuildingEnergyDto;
import com.ThreeZem.three_zem_back.service.DataGenerationService;
import com.ThreeZem.three_zem_back.service.RealTimeDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RealTimeDataController implements DisposableBean {

    private final RealTimeDataService realTimeDataService;

    // 애플리케이션 종료 시 SSE 스트림을 중단시키기 위한 신호
    private final Sinks.Empty<Void> shutdownSignal = Sinks.empty();

    // 애플리케이션 종료 시 호출되어 shutdownSignal을 보내 SSE 스트림을 정상적으로 종료시킨다
    @Override
    public void destroy() {
        shutdownSignal.tryEmitEmpty();
    }

    /// Flux 설정
    private Flux<BuildingEnergyDto> createEnergyFlux(Supplier<BuildingEnergyDto> supplier) {
        return Flux.interval(Duration.ofSeconds(ConfigConst.DATA_UPDATE_MS / 1000))
                .flatMap(seq -> {
                    try {
                        BuildingEnergyDto data = supplier.get();
                        // 데이터가 null인 경우 (서버 시작 중 데이터가 아직 준비되지 않음)
                        if (data == null) {
                            log.warn("[WARN] 실시간 데이터를 아직 사용할 수 없습니다. 이번 전송은 건너뜁니다.");
                            return Flux.empty(); // 클라이언트 에러 없이 스트림 유지
                        }
                        return Flux.just(data);
                    } catch (Exception e) {
                        // 그 외 예상치 못한 에러
                        log.error("[Error] 실시간 데이터 생성 중 예상치 못한 오류가 발생했습니다.", e);
                        return Flux.empty(); // 클라이언트 에러 없이 스트림 유지
                    }
                })
                .takeUntilOther(shutdownSignal.asMono()); // 종료 신호를 받으면 스트림을 중단
    }

    /// 실시간 에너지 사용량 조회
    @GetMapping(value = "/api/energy/sse/all", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BuildingEnergyDto> getSseEnergyAll(Authentication auth) {
        return createEnergyFlux(realTimeDataService::getBuildingEnergyData);
    }

    /// 실시간 금일 장비(전력) 누적 사용량 조회
    @GetMapping(value = "/api/energy/sse/day/device", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BuildingEnergyDto> getSseEnergyDayDevice() {
        return createEnergyFlux(() -> null);
    }

    /// 실시간 금일 가스 누적 사용량 조회
    @GetMapping(value = "/api/energy/sse/day/gas", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BuildingEnergyDto> getSseEnergyDayGas() {
        return createEnergyFlux(() -> null);
    }

    /// 실시간 금일 수도 누적 사용량 조회
    @GetMapping(value = "/api/energy/sse/day/water", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BuildingEnergyDto> getSseEnergyDayWater() {
        return createEnergyFlux(() -> null);
    }

    /// 실시간 금월 장비(전력) 누적 사용량 조회
    @GetMapping(value = "/api/energy/sse/month/device", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BuildingEnergyDto> getSseEnergyMonthDevice() {
        return createEnergyFlux(() -> null);
    }

    /// 실시간 금월 가스 누적 사용량 조회
    @GetMapping(value = "/api/energy/sse/month/gas", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BuildingEnergyDto> getSseEnergyMonthGas() {
        return createEnergyFlux(() -> null);
    }

    /// 실시간 금월 수도 누적 사용량 조회
    @GetMapping(value = "/api/energy/sse/month/water", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BuildingEnergyDto> getSseEnergyMonthWater() {
        return createEnergyFlux(() -> null);
    }

}
