package com.example.miniproject.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.miniproject.member.domain.TotalAnnual;

public interface TotalAnnualRepository extends JpaRepository<TotalAnnual, Long> {
	@Query("SELECT t FROM TotalAnnual t WHERE t.years = (SELECT t2.years FROM TotalAnnual t2 WHERE t2.years <= :years)")
	Optional<TotalAnnual> findAnnualAmountByYears(int years);
}
