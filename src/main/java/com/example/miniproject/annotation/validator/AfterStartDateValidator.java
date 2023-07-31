package com.example.miniproject.annotation.validator;

import java.time.LocalDate;

import com.example.miniproject.annotation.AfterStartDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AfterStartDateValidator implements ConstraintValidator<AfterStartDate, Object> {
	@Override
	public boolean isValid(Object object, ConstraintValidatorContext context) {
		try {
			LocalDate startDate = LocalDate.parse(object.getClass().getField("startDate").toString());
			LocalDate endDate = LocalDate.parse(object.getClass().getField("endDate").toString());

			if (startDate == null || endDate == null)
				return false;

			return endDate.isAfter(startDate);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void initialize(AfterStartDate constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}
}
