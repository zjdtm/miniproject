package com.example.miniproject.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniproject.admin.dto.AdminRequestDto;
import com.example.miniproject.admin.dto.AdminResponseDto;
import com.example.miniproject.annual.domain.Annual;
import com.example.miniproject.annual.repository.AnnualRepository;
import com.example.miniproject.constant.ErrorCode;
import com.example.miniproject.constant.Status;
import com.example.miniproject.exception.AnnualException;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
	private final AnnualRepository annualRepository;
	private final EntityManager entityManager;

	public List<AdminResponseDto.MainDto> getAnnuals() {
		List<Annual> annuals = annualRepository.findAll();

		List<AdminResponseDto.MainDto> mainDtos = annuals.stream()
			.map(annual -> new AdminResponseDto.MainDto(annual))
			.toList();

		return mainDtos;
	}

	@Transactional
	public boolean updateStatus(AdminRequestDto.ApplyDto applyDto) {
		Annual annualPs = entityManager.find(Annual.class, applyDto.getId());

		if (annualPs == null)
			throw new AnnualException(ErrorCode.ANNUAL_NOT_FOUND);

		annualPs.setStatus(Status.COMPLETE);

		return true;
	}
}
