package com.example.miniproject.exception;

import com.example.miniproject.constant.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnnualException extends DomainException {

	public AnnualException(ErrorCode errorCode) {
		super(errorCode);
	}
}
