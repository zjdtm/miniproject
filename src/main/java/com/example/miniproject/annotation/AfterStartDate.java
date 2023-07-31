package com.example.miniproject.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterStartDate {
	String message() default "종료일은 시작일보다 이전 날짜일 수 없습니다.";
}
