package com.example.miniproject.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<?> handlerDomainException(DomainException e) {
        log.error(e.getMessage());
        ErrorResponse response = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    // MethodArgumentNotValidException 에러 발생시 처리하는 handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handlerValidationException(MethodArgumentNotValidException e) {
        ObjectError error = e.getBindingResult().getAllErrors().stream().findFirst().get();

        log.error(e.getMessage());
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .status(e.getStatusCode().value())
                        .message(error.getDefaultMessage())
                        .build(),
                HttpStatusCode.valueOf(e.getStatusCode().value())
        );
    }

    // 회원가입시 날짜 형식이 맞지 않으면 처리하는 handler
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<?> handlerDateTimeParseException(
            HttpMessageNotReadableException e
    ) {

        String errorMessage = "날짜 형식이 올바르지 않습니다. (yyyy-MM-dd)";

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);

        log.error(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    }
}
