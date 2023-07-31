package com.example.miniproject.admin.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.miniproject.admin.dto.AdminResponseDto;
import com.example.miniproject.admin.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
	private final AdminService adminService;

	public List<AdminResponseDto.MainDto> main() {
		List<AdminResponseDto.MainDto> mainDtos = adminService.getAnnuals();

		return mainDtos;
	}
}
