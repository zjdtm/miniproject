package com.example.miniproject.annotation.validator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

import com.example.miniproject.annotation.AfterStartDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AfterStartDateValidator implements ConstraintValidator<AfterStartDate, Object> {
	@Override
	public boolean isValid(Object object, ConstraintValidatorContext context) {
		try {
			Method getStartDateMethod = object.getClass().getMethod("getStartDate");
			Method getEndDateMethod = object.getClass().getMethod("getEndDate");

			LocalDate startDate = LocalDate.parse((String)getStartDateMethod.invoke(object));
			LocalDate endDate = LocalDate.parse((String)getEndDateMethod.invoke(object));
			if (startDate == null || endDate == null)
				return false;

			return endDate.isAfter(startDate) || endDate.isEqual(startDate);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initialize(AfterStartDate constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}
}
