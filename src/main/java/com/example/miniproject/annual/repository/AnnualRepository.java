package com.example.miniproject.annual.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.miniproject.annual.domain.Annual;

public interface AnnualRepository extends JpaRepository<Annual, Long> {
}
