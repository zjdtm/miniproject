package com.example.miniproject.admin.dto;

import java.time.format.DateTimeFormatter;

import com.example.miniproject.annual.domain.Annual;

public class AdminResponseDto {
	public static class MainDto {
		private Long id;
		private String name;
		private String category;
		private String startDate;
		private String endDate;
		private String reason;
		private String status;

		public MainDto(Annual annual) {
			this.id = annual.getId();
			this.name = annual.getMember().getName();
			this.category = annual.getCategory().name();
			this.startDate = annual.getStartedAt().format(DateTimeFormatter.ISO_DATE);
			this.endDate = annual.getLastedAt().format(DateTimeFormatter.ISO_DATE);
			this.reason = annual.getReason().name();
			this.status = annual.getStatus().name();
		}
	}
}
