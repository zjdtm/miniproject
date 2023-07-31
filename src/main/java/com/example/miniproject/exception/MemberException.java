package com.example.miniproject.exception;

import org.springframework.http.HttpStatus;

import com.example.miniproject.constant.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberException extends DomainException {
	private ErrorCode errorCode;
	private HttpStatus httpStatus;
	private String message;

	public MemberException(ErrorCode errorCode) {
		super(errorCode);
	}
}
