package com.example.miniproject.annual.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.miniproject.annual.service.AnnualService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AnnualController {
	private final AnnualService annualService;
}
