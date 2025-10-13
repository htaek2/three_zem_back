package com.ThreeZem.three_zem_back.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
/// 시간 유형 열거형
public enum DateTimeType {

    /// 시간
    HOUR((byte) 0),
    /// 일
    DAY((byte) 1),
    /// 월
    MONTH((byte) 2),
    /// 년
    YEAR((byte) 3);

    private byte value;
}
