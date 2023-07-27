package com.example.miniproject.annual.dto;

import java.time.format.DateTimeFormatter;

import com.example.miniproject.annual.domain.Annual;

import lombok.Getter;
import lombok.Setter;

public class AnnualResponseDto {
	@Getter
	@Setter
	public static class MainDto {
		private Long id;
		private String name;
		private String category;
		private String startDate;
		private String endDate;
		private String reason;

		public MainDto(Annual annual) {
			this.id = annual.getId();
			this.name = annual.getMember().getName();
			this.category = annual.getCategory().getName();
			this.startDate = annual.getStartedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			this.endDate = annual.getLastedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			this.reason = annual.getReason() == null ? null : annual.getReason().getName();
		}
	}
}
