package org.example.common.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    // 전역 예외 처리 핸들러

    // Bad Request ( Json 형식 부합 )
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            @NonNull HttpMessageNotReadableException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        log.error("잘못된 JSON 형식으로 요청이 들어왔습니다.", ex);

        ErrorResponse response = new ErrorResponse(ErrorCode.BAD_REQUEST);
        response = response.withCustomMessage("JSON 형식이 잘못되었습니다. 요청 데이터를 확인하세요.");

        return ResponseEntity.badRequest().body(response);
    }

    // Bad Request ( Vaild 조건 부합 )
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        log.error("Bad Request error ( Valid err )", ex);
        ErrorResponse response = new ErrorResponse(ErrorCode.BAD_REQUEST);

        // 필드 에러 메시지 수집
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getDefaultMessage())
                .collect(Collectors.toList());

        // 새로운 ErrorResponse 객체 생성
        ErrorResponse updatedResponse = response.withCustomMessage(errors.toString());

        return ResponseEntity.badRequest().body(updatedResponse);
    }

    // Default
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        log.error("Internal Server error", ex);
        ErrorResponse response = new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}
