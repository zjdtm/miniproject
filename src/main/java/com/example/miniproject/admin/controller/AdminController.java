package com.example.miniproject.admin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.miniproject.admin.dto.AdminRequestDto;
import com.example.miniproject.admin.dto.AdminResponseDto;
import com.example.miniproject.admin.service.AdminService;
import com.example.miniproject.member.domain.PrincipalDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
	private final AdminService adminService;

	@GetMapping("")
	public List<AdminResponseDto.MainDto> main() {
		List<AdminResponseDto.MainDto> mainDtos = adminService.getAnnuals();

		return mainDtos;
	}

	@PostMapping("/apply")
	public ResponseEntity<?> apply(@RequestBody @Valid AdminRequestDto.ApplyDto applyDto, @AuthenticationPrincipal
	PrincipalDetails principalDetails) {
		adminService.updateStatus(applyDto, principalDetails.getUsername());

		return ResponseEntity.ok().build();
	}
}
