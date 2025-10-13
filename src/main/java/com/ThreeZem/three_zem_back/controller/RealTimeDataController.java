package com.ThreeZem.three_zem_back.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalTime;

public class RealTimeDataController {

    /// SSE(Server-Sent Event) 방식으로 구독한 클라이언트에게 10초마다 데이터를 전송한다.
    // MediaType.TEXT_EVENT_STREAM_VALUE 로 설정해야 sse 방식이 됨.
    @GetMapping(value = "/api/sse/energy", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamEnergyData() {

        // db에서 층마다 모든 전자기기를 가져와서
        // 기기 상태가 on이면 노이즈 + 랜덤 사용량 + 이벤트 => 현재 사용량
        // id, type, 사용량을 넣기
        // 수도 사용량
        // 층마다 에너지 사용량 다 넣고, 가스 사용량도 넣고
        // 클라이언트에 넘겨주기

//        var data = new RealTimeDataDto();



        return Flux.interval(Duration.ofSeconds(1)).map(seq -> "현재 시간: " + LocalTime.now());
    }

}
