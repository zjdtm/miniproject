package com.example.miniproject.annual.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.miniproject.annual.domain.Annual;
import com.example.miniproject.constant.Category;
import com.example.miniproject.constant.Status;
import com.example.miniproject.member.domain.Member;

public interface AnnualRepository extends JpaRepository<Annual, Long> {
	List<Annual> findAllByMemberAndCategory(Member member, Category category);

	List<Annual> findAllByStatus(Status status);

	@Query("SELECT count(a) FROM Annual a "
		+ "  WHERE ((a.startedAt < :endDate AND a.lastedAt > :startDate) OR (a.startedAt = :startDate and a.lastedAt = :endDate)) AND a.member = :member AND a.status = :status")
	int countByStartedAtAndLastedAt(LocalDate startDate, LocalDate endDate, Member member, Status status);
}
