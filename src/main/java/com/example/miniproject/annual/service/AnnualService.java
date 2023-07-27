package com.example.miniproject.annual.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.miniproject.annual.domain.Annual;
import com.example.miniproject.annual.dto.AnnualResponseDto;
import com.example.miniproject.annual.repository.AnnualRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnualService {
	private final AnnualRepository annualRepository;

	public List<AnnualResponseDto.MainDto> findAll() {
		List<Annual> annuals = annualRepository.findAll();

		return annuals.stream().map(annual -> new AnnualResponseDto.MainDto(annual)).collect(Collectors.toList());
	}
}
