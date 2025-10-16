package com.ThreeZem.three_zem_back.handler;

import com.ThreeZem.three_zem_back.data.message.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /// 전역 오류 핸들러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        // 요청의 Accept 헤더를 확인하여 SSE 요청인지 구분
        String acceptHeader = request.getHeader("Accept");
        boolean isSseRequest = acceptHeader != null && acceptHeader.contains(MediaType.TEXT_EVENT_STREAM_VALUE);

        if (isSseRequest) {
            // SSE 스트림에서 예외가 발생한 경우, JSON 본문을 보내려고 시도하지 않음
            // 에러를 로그에 기록하고, 클라이언트가 연결을 다시 시도할 수 있도록 스트림을 종료
            log.error("[ERROR] SSE 전송 중 에러 발생: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 일반 REST API 요청의 경우, 기존과 같이 JSON 형식의 에러 메시지를 반환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorMessage message = new ErrorMessage(
                status.value(),
                LocalDateTime.now().format(formatter),
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(message, status);
    }
}