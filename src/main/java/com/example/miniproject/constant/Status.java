package com.example.miniproject.constant;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
	READY("결재 대기"), COMPLETE("결재 완료");

	private String name;

	public static Status findByName(String name) {
		return Arrays.stream(Status.values())
			.filter(status -> status.name.equals(name))
			.findFirst()
			.orElseThrow();
	}
}
