package com.example.miniproject.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
	ANNUAL("연차"), DUTY("당직");

	private String name;
}
