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

	// public void setAnnualTotalByMember(Member member) {
	// 	String queryString = "SELECT a.annual_total " +
	// 		"FROM annual a " +
	// 		"WHERE (a.years, a.hist_year) IN (" +
	// 		"SELECT MAX(a2.years), MAX(a2.hist_year) " +
	// 		"FROM annual a2 " +
	// 		"WHERE a2.years <= (SELECT TIMESTAMPDIFF(YEAR, m.joined_at, NOW()) FROM member m WHERE m.email = :email)" +
	// 		")";
	// 	Query nativeQuery = entityManager.createNativeQuery(queryString);
	// 	nativeQuery.setParameter("email", member.getEmail());
	// 	List<Integer> results = nativeQuery.getResultList();
	//
	// 	if (results == null || results.size() == 0)
	// 		throw new AnnualException(ErrorCode.ANNUAL_TOTAL_NOT_FOUND);
	//
	// 	member.setAnnualTotal(results.get(0));
	// }

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
}
