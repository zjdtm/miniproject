package com.example.miniproject.exception;

import org.springframework.http.HttpStatus;

import com.example.miniproject.constant.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DomainException extends RuntimeException {
	private ErrorCode errorCode;
	private HttpStatus httpStatus;
	private String message;

	public DomainException(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.httpStatus = errorCode.getStatus();
		this.message = errorCode.getMessage();
	}
}
