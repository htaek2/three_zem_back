package com.ThreeZem.three_zem_back.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
/// 장비 유형 열거형
public enum DeviceType {

    /// 컴퓨터(+모니터)
    COMPUTER((byte) 0),
    /// 냉난방기
    AIR_CONDITIONER((byte) 1),
    /// 조명
    LIGHT((byte) 2);

    private byte value;

    public static DeviceType fromByte(byte value) {
        for (DeviceType type : DeviceType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown device type value: " + value);
    }
}
