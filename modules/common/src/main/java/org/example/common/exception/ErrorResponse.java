package org.example.common.exception;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int statusCode;
    private final String error;
    private final String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.statusCode = errorCode.getHttpStatus().value();
        this.error = errorCode.getHttpStatus().name();
        this.message = errorCode.getMessage();
    }

    // 팩토리 메서드: 기존 객체의 데이터를 유지하면서 message를 수정한 새 객체를 생성
    public ErrorResponse withCustomMessage(String customMessage) {
        return new ErrorResponse(this.statusCode, this.error, customMessage);
    }

    // 객체 생성 용
    private ErrorResponse(int statusCode, String error, String message) {
        this.statusCode = statusCode;
        this.error = error;
        this.message = message;
    }

}
