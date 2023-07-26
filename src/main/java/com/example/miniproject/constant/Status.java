package com.example.miniproject.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
	READY("결재 대기"), COMPLEATE("결재 완료");

	private String name;
}
