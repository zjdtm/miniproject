package com.example.miniproject.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.miniproject.admin.dto.AdminRequestDto;
import com.example.miniproject.admin.dto.AdminResponseDto;
import com.example.miniproject.annual.domain.Annual;
import com.example.miniproject.annual.repository.AnnualRepository;
import com.example.miniproject.constant.Status;

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

	public boolean updateStatus(AdminRequestDto.ApplyDto applyDto) {
		Annual annualPs = entityManager.find(Annual.class, applyDto.getId());

		//@TODO: Exception 처리
		if (annualPs == null)
			return false;

		//@TODO: 결재 반려는 없는지? 확인
		annualPs.setStatus(Status.COMPLETE);

		return true;
	}
}
