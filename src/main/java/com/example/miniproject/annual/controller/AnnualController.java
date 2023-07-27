package com.example.miniproject.annual.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.miniproject.annual.dto.AnnualResponseDto;
import com.example.miniproject.annual.service.AnnualService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AnnualController {
	private final AnnualService annualService;

	@GetMapping("/main")
	public List<AnnualResponseDto.MainDto> main() {
		List<AnnualResponseDto.MainDto> mainDtos = annualService.findAll();

		return mainDtos;
	}
}
