package com.ThreeZem.three_zem_back.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WeatherDto {

    /// 현재 온도
    private float nowTemperature;

    /// 습도
    private float humidity;

    /// 풍속
    private float windSpeed;

    /// 날씨 상태
    private byte weatherStatus;

}
