package com.ThreeZem.three_zem_back.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/// 전역에서 공통으로 사용하기 위한 함수들의 클래스
public class CommonUtil {

    /// 시간 관련 함수 클래스
    public static class TimeUtil {

        /// 시간 Fomatter를 반환한다.
        public static DateTimeFormatter getDateTimeFormatter() {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        }

        public static String getNowDate() {
            return LocalDateTime.now().format(getDateTimeFormatter());
        }

    }

}
