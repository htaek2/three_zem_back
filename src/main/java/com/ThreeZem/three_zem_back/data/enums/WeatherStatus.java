package com.ThreeZem.three_zem_back.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WeatherStatus {
    SUNNY((byte)0),
    RAINY((byte)1),
    SNOWY((byte)2),
    CLOUDY((byte)3);

    private byte value;
}
