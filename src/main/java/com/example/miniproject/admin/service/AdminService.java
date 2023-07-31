package com.example.miniproject.admin.service;

import java.util.List;

import com.example.miniproject.admin.dto.AdminResponseDto;
import com.example.miniproject.annual.domain.Annual;
import com.example.miniproject.annual.repository.AnnualRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminService {
	private final AnnualRepository annualRepository;

	public List<AdminResponseDto.MainDto> getAnnuals() {
		List<Annual> annuals = annualRepository.findAll();

		List<AdminResponseDto.MainDto> mainDtos = annuals.stream()
			.map(annual -> new AdminResponseDto.MainDto(annual))
			.toList();

		return mainDtos;
	}
}
