package com.ThreeZem.three_zem_back.controller;

import com.ThreeZem.three_zem_back.data.dto.WeatherDto;
import com.ThreeZem.three_zem_back.service.ExternalApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ExternalApiController {

    private final ExternalApiService externalApiService;

    @GetMapping("/api/weather/now")
    public ResponseEntity<WeatherDto> getWeather() {
        return externalApiService.getWeather(133);
    }

}
