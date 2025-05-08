package org.example.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // default
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request."), // 잘못된 요청
    UNAUTHORIZED_REQUEST(HttpStatus.UNAUTHORIZED, "Unauthorized."), // 인증되지 않은 사용자의 요청
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "Forbidden."), // 권한이 없는 사용자의 요청
    NOT_FOUND(HttpStatus.NOT_FOUND, "Not found."), // 리소스를 찾을 수 없음
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Not allowed method."), // 허용되지 않은 Request Method 호출
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Server error."); // 내부 서버 오류

    private final HttpStatus httpStatus;
    private final String message;
}
