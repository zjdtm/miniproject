package com.example.miniproject.annual.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.miniproject.annual.dto.AnnualRequestDto;
import com.example.miniproject.annual.dto.AnnualResponseDto;
import com.example.miniproject.annual.service.AnnualService;
import com.example.miniproject.member.domain.PrincipalDetails;

import jakarta.validation.Valid;
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

	@PostMapping("/annual")
	public ResponseEntity<?> save(@RequestBody @Valid AnnualRequestDto.SaveDto saveDto, @AuthenticationPrincipal
	PrincipalDetails principalDetails) {
		annualService.createAnnual(saveDto, principalDetails.getUsername());

		return ResponseEntity.ok().build();
	}
}
