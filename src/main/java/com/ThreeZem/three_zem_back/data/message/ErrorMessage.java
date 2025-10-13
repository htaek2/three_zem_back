package com.ThreeZem.three_zem_back.data.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

/// 에러 메시지 양식
@Getter
@AllArgsConstructor
public class ErrorMessage {
    private int status;
    private String timestamp;
    private String message;
    private String description;
}
