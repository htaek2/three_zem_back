package com.ThreeZem.three_zem_back.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlertType {

    /// 에너지 소비 이상 패턴 알림
    OUT_PATTERN((byte) 0);

    private byte value;

}
