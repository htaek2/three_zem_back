package com.ThreeZem.three_zem_back.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
/// 장비 상태 열거형
public enum DeviceStatus {

    /// 장비 연결 해제
    DEVICE_OFFLINE((byte) 0),
    /// 장비 꺼짐
    DEVICE_OFF((byte) 1),
    /// 장비 켜짐
    DEVICE_ON((byte) 2);

    private byte value;

}