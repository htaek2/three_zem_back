package com.ThreeZem.three_zem_back.data.dto.energy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
/// 시간/일/월/년 에너지 데이터 조회 시 사용
public class ReadingDto {

    /// 날짜
    private LocalDateTime timestamp;

    /// 사용량
    private float usage;
}
