package com.example.miniproject.annual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.miniproject.annual.domain.Annual;
import com.example.miniproject.constant.Category;
import com.example.miniproject.member.domain.Member;

public interface AnnualRepository extends JpaRepository<Annual, Long> {
	List<Annual> findAllByMemberAndCategory(Member member, Category category);
}
