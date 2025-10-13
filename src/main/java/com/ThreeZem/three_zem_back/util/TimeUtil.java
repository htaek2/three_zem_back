package com.ThreeZem.three_zem_back.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/// 시간 관련 함수 클래스
public class TimeUtil {

    /// 시간 Fomatter를 반환한다.
    public static DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    }

    /// 현재 시간을 가져온다.
    public static String getNowDateStr() {
        return LocalDateTime.now().format(getDateTimeFormatter());
    }

}
