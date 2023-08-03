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
import com.example.miniproject.exception.MemberException;
import com.example.miniproject.member.domain.Member;
import com.example.miniproject.member.repository.MemberRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
	private final AnnualRepository annualRepository;
	private final MemberRepository memberRepository;
	private final EntityManager entityManager;

	public List<AdminResponseDto.MainDto> getAnnuals() {
		List<Annual> annuals = annualRepository.findAll();

		List<AdminResponseDto.MainDto> mainDtos = annuals.stream()
			.map(annual -> new AdminResponseDto.MainDto(annual))
			.toList();

		return mainDtos;
	}

	@Transactional
	public boolean updateStatus(AdminRequestDto.ApplyDto applyDto, String email) {
		Annual annualPs = entityManager.find(Annual.class, applyDto.getId());

		if (annualPs == null)
			throw new AnnualException(ErrorCode.ANNUAL_NOT_FOUND);

		annualPs.setStatus(Status.COMPLETE);

		updateAnnualAmount(annualPs.getMember().getEmail());

		return true;
	}

	private void updateAnnualAmount(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

		member.sumAnnualRemain(-1);
		member.sumAnnualUsed(1);
	}
}
