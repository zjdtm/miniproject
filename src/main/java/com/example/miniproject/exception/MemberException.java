package com.example.miniproject.exception;

import com.example.miniproject.constant.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberException extends DomainException {

    private ErrorCode errorCode;

    public MemberException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
