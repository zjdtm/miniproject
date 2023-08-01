package com.example.miniproject.constant;

import java.util.Arrays;

import com.example.miniproject.exception.AnnualException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
	ANNUAL("연차"), DUTY("당직");

	private String name;

	public static Category findByName(String name) {
		return Arrays.stream(Category.values())
			.filter(category -> category.name.equals(name))
			.findFirst()
			.orElseThrow(() -> new AnnualException(ErrorCode.CATEGORY_NOT_FOUND));
	}
}
