package com.example.miniproject.annual;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AnnualController {
	private final AnnualService annualService;
}
