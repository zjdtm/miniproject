package com.example.miniproject.annual.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.miniproject.annual.domain.Annual;
import com.example.miniproject.annual.dto.AnnualRequestDto;
import com.example.miniproject.annual.dto.AnnualResponseDto;
import com.example.miniproject.annual.repository.AnnualRepository;
import com.example.miniproject.constant.ErrorCode;
import com.example.miniproject.exception.MemberException;
import com.example.miniproject.member.domain.Member;
import com.example.miniproject.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnualService {
	private final AnnualRepository annualRepository;
	private final MemberRepository memberRepository;

	public List<AnnualResponseDto.MainDto> findAll() {
		List<Annual> annuals = annualRepository.findAll();

		return annuals.stream().map(annual -> new AnnualResponseDto.MainDto(annual)).collect(Collectors.toList());
	}

	public void createAnnual(AnnualRequestDto.SaveDto saveDto, String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

		Annual annual = saveDto.toEntity(member);

		annualRepository.save(annual);
	}
}
