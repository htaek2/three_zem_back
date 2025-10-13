package com.ThreeZem.three_zem_back.handler;

import com.ThreeZem.three_zem_back.data.message.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@ControllerAdvice
public class GlobalExceptionHandler {

    /// 전역 오류 핸들러
    /// 오류 발생시 클라이언트에게 오류 정보를 보여줌.
    /// 일단 400 오류로 냄.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGlobalException(Exception ex, WebRequest request){
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
