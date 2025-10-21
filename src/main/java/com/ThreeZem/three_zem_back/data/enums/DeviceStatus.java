package com.ThreeZem.three_zem_back.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
/// 장비 상태 열거형
public enum DeviceStatus {

    /// 장비 꺼짐
    DEVICE_OFF((byte) 0),
    /// 장비 켜짐
    DEVICE_ON((byte) 1);

    private byte value;

    public static DeviceStatus fromByte(byte value) {
        for (DeviceStatus type : DeviceStatus.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("[Error] 잘못된 DeviceStatus: " + value);
    }

}