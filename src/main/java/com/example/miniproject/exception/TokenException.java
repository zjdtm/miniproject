package com.example.miniproject.exception;

import com.example.miniproject.constant.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class TokenException extends DomainException {

	private ErrorCode errorCode;

	public TokenException(ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;

	}
}
