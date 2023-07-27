package com.example.miniproject.constant;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Reason {
	ANNUAL("연차유급휴가"),
	MATERNITY("출산휴가"),
	CONDOLENCE("경조사휴가"),
	SICK("병가휴가"),
	OTHER("기타휴가");

	private String name;

	public static Reason findByName(String name) {
		return Arrays.stream(Reason.values())
			.filter(reason -> reason.name.equals(name))
			.findFirst()
			.orElseThrow();
	}
}
