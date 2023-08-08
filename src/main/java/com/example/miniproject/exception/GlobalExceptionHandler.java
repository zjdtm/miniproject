package com.example.miniproject.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<?> handlerDomainException(DomainException e) {
        log.error(e.getMessage());
        ErrorResponse response = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

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

}
