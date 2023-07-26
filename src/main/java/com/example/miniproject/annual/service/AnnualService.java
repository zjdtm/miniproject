package com.example.miniproject.annual.service;

import org.springframework.stereotype.Service;

import com.example.miniproject.annual.repository.AnnualRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnualService {
	private final AnnualRepository annualRepository;
}
