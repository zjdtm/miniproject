package com.example.miniproject.annual.repository;

import com.example.miniproject.annual.domain.Annual;
import com.example.miniproject.constant.Category;
import com.example.miniproject.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnualRepository extends JpaRepository<Annual, Long> {
    List<Annual> findAllByMemberAndCategory(Member member, Category category);
}
