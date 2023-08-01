package com.example.miniproject.admin.dto;

import java.time.format.DateTimeFormatter;

import com.example.miniproject.annual.domain.Annual;
import com.example.miniproject.util.AESUtil;

import lombok.Getter;
import lombok.Setter;

public class AdminResponseDto {
	@Getter
	@Setter
	public static class MainDto {
		private Long id;
		private String name;
		private String title;
		private String category;
		private String startDate;
		private String endDate;
		private String reason;
		private String status;

		public MainDto(Annual annual) {
			this.id = annual.getId();
			this.name = AESUtil.decrypt(annual.getMember().getName());
			this.title = annual.getTitle();
			this.category = annual.getCategory().getName();
			this.startDate = annual.getStartedAt().format(DateTimeFormatter.ISO_DATE);
			this.endDate = annual.getLastedAt().format(DateTimeFormatter.ISO_DATE);
			this.reason = annual.getReason() == null ? null : annual.getReason().getName();
			this.status = annual.getStatus().getName();
		}
	}
}
