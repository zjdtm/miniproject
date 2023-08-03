package com.example.miniproject.annual.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniproject.annual.domain.Annual;
import com.example.miniproject.annual.dto.AnnualRequestDto;
import com.example.miniproject.annual.dto.AnnualResponseDto;
import com.example.miniproject.annual.repository.AnnualRepository;
import com.example.miniproject.constant.Category;
import com.example.miniproject.constant.ErrorCode;
import com.example.miniproject.exception.AnnualException;
import com.example.miniproject.exception.MemberException;
import com.example.miniproject.member.domain.Member;
import com.example.miniproject.member.repository.MemberRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnualService {
	private final AnnualRepository annualRepository;
	private final MemberRepository memberRepository;
	private final EntityManager entityManager;

	public List<AnnualResponseDto.MainDto> findAll() {
		List<Annual> annuals = annualRepository.findAll();

		return annuals.stream().map(annual -> new AnnualResponseDto.MainDto(annual)).collect(Collectors.toList());
	}

	@Transactional
	public void createAnnual(AnnualRequestDto.SaveDto saveDto, String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

		Annual annual = saveDto.toEntity(member);

		annualRepository.save(annual);
	}

	public void findAllByMember(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

		List<Annual> annuals = annualRepository.findAllByMemberAndCategory(member, Category.ANNUAL);
		List<Annual> duties = annualRepository.findAllByMemberAndCategory(member, Category.DUTY);

	}

	@Transactional
	public void deleteAnnual(AnnualRequestDto.CancelDto cancelDto, String email) {
		Annual annual = annualRepository.findById(cancelDto.getId())
			.orElseThrow(() -> new AnnualException(ErrorCode.ANNUAL_NOT_FOUND));

		if (annual.getMember().isNotEqualsEmail(email))
			throw new AnnualException(ErrorCode.MEMBER_NOT_MATCHED);

		annualRepository.delete(annual);
	}

	@Transactional
	public void updateAnnual(AnnualRequestDto.UpdateDto updateDto, String email) {
		Annual annual = annualRepository.findById(updateDto.getId())
			.orElseThrow(() -> new AnnualException(ErrorCode.ANNUAL_NOT_FOUND));

		if (annual.getMember().isNotEqualsEmail(email))
			throw new MemberException(ErrorCode.MEMBER_NOT_MATCHED);

		annual.updateData(updateDto);
	}

	public AnnualResponseDto.MyPageDto findAnnualsByMember(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

		List<Annual> annuals = findAllByMemberAndCategory(member, Category.ANNUAL);
		List<Annual> duties = findAllByMemberAndCategory(member, Category.DUTY);

		List<AnnualResponseDto.AnnualHistroy> annualHistroys = annuals.stream()
			.map(annual -> new AnnualResponseDto.AnnualHistroy(annual))
			.collect(
				Collectors.toList());
		;

		List<AnnualResponseDto.DutyHistory> dutyHistories = duties.stream()
			.map(duty -> new AnnualResponseDto.DutyHistory(duty))
			.collect(
				Collectors.toList());
		;

		return new AnnualResponseDto.MyPageDto(member, annualHistroys, dutyHistories);
	}

	private List<Annual> findAllByMemberAndCategory(Member member, Category category) {
		List<Annual> annuals = annualRepository.findAllByMemberAndCategory(member, category);

		return annuals;
	}
}
