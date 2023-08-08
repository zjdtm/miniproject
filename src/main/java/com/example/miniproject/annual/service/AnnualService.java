package com.example.miniproject.annual.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniproject.annual.domain.Annual;
import com.example.miniproject.annual.dto.AnnualRequestDto;
import com.example.miniproject.annual.dto.AnnualResponseDto;
import com.example.miniproject.annual.repository.AnnualRepository;
import com.example.miniproject.constant.Category;
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
public class AnnualService {
	private final AnnualRepository annualRepository;
	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final EntityManager entityManager;

	public AnnualResponseDto.MainDto findAll(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(ErrorCode.TOKEN_NOT_FOUND));

		List<Annual> annuals = annualRepository.findAllByStatus(Status.COMPLETE);

		return new AnnualResponseDto.MainDto(member.getName(), annuals);
	}

	@Transactional
	public void createAnnual(AnnualRequestDto.SaveDto saveDto, String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

		if (isNotRemainAnnualAmount(saveDto.getStartDate(), saveDto.getEndDate(), member.getAnnualRemain()))
			throw new AnnualException(ErrorCode.ANNUAL_NOT_BALANCED_AMOUNT);

		if (isExistAnnual(saveDto.getStartDate(), saveDto.getEndDate(), member, Status.COMPLETE))
			throw new AnnualException(ErrorCode.ANNUAL_DATE_DUPLICATED);

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

		List<AnnualResponseDto.DutyHistory> dutyHistories = duties.stream()
			.map(duty -> new AnnualResponseDto.DutyHistory(duty))
			.collect(
				Collectors.toList());
		;

		return new AnnualResponseDto.MyPageDto(member, annualHistroys, dutyHistories);
	}

	@Transactional
	public void updatePassword(AnnualRequestDto.UpdatePasswordDto updatePasswordDto, String email) {

		// 이메일로 회원의 정보 찾기
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

		// 회원의 비밀번호를 암호화 한 후에 저장
		member.changePassword(passwordEncoder.encode(updatePasswordDto.getNewPassword()));

	}

	private List<Annual> findAllByMemberAndCategory(Member member, Category category) {
		List<Annual> annuals = annualRepository.findAllByMemberAndCategory(member, category);

		return annuals;
	}

	private boolean isExistAnnual(String startDate, String endDate, Member member, Status status) {
		LocalDate startLocalDate = LocalDate.parse(startDate);
		LocalDate endLocalDate = LocalDate.parse(endDate);

		int count = annualRepository.countByStartedAtAndLastedAt(startLocalDate, endLocalDate, member, status);

		return count > 0;
	}

	private boolean isNotRemainAnnualAmount(String startDate, String endDate, int annualRemain) {
		LocalDateTime startLocalDate = LocalDate.parse(startDate).atStartOfDay();
		LocalDateTime endLocalDate = LocalDate.parse(endDate).atStartOfDay();

		int betweenDays = (int)Duration.between(startLocalDate, endLocalDate).toDays();

		return annualRemain - betweenDays < 0;
	}
}
