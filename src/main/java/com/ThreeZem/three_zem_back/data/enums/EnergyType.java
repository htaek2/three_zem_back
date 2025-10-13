package com.ThreeZem.three_zem_back.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
/// 에너지 유형 열거형
public enum EnergyType {
    
    /// 전력
    ELECTRICITY((byte) 0),
    /// 가스
    GAS((byte) 1),
    /// 수도
    WATER((byte) 2);
    
    private byte value;
}
