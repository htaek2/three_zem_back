package com.ThreeZem.three_zem_back.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
/// 계측 단위 열거형
public enum ReadingTarget {

    /// 빌딩
    BUILDING((byte) 0),
    /// 층
    FLOOR((byte) 1),
    /// 장비
    DEVICE((byte) 2);

    private byte value;
}
