package com.example.miniproject.constant;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메서드입니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "접근할 수 있는 권한이 없습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "응답이 없습니다. 잠시 후 다시 시도해주세요."),
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 정보가 없습니다. 로그인 후 다시 시도해주세요."),
	ANNUAL_NOT_FOUND(HttpStatus.NOT_FOUND, "연차/당직 정보가 없습니다."),
	MEMBER_EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "해당 이메일로 가입된 회원이 있습니다."),
	CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "카테고리를 찾을 수 없습니다."),
	REASON_NOT_FOUND(HttpStatus.BAD_REQUEST, "연차 종류를 찾을 수 없습니다."),
	MEMBER_PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
	MEMBER_IS_LOGOUT(HttpStatus.BAD_REQUEST, "이미 로그아웃된 상태입니다."),
	TOKEN_NOT_FOUND(HttpStatus.FORBIDDEN, "인증 정보가 만료되었습니다."),
	TOKEN_NOT_MATCH(HttpStatus.BAD_REQUEST, "인증 정보가 일치하지 않습니다."),
	ANNUAL_TOTAL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자가 사용 가능한 총 연차 수를 찾을 수 없습니다."),
	MEMBER_NOT_MATCHED(HttpStatus.FORBIDDEN, "연차/당직 수정, 삭제는 작성한 사용자만 가능합니다.");

	private HttpStatus status;
	private String message;
}
