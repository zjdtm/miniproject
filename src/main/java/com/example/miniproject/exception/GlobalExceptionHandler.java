package com.example.miniproject.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({MemberException.class, AnnualException.class, TokenException.class})
    public ResponseEntity<?> handlerMemberException(DomainException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }
}
